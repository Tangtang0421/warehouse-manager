package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import com.qlx.oa.dto.RecordAddDTO;
import com.qlx.oa.dto.RecordPageDTO;
import com.qlx.oa.po.Goods;
import com.qlx.oa.po.Record;
import com.qlx.oa.po.User;
import com.qlx.oa.service.IGoodsService;
import com.qlx.oa.service.IRecordService;
import com.qlx.oa.service.IUserService;
import com.qlx.oa.vo.RecordResVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
    private final IRecordService recordService;
    private final IUserService userService;
    private final IGoodsService goodsService;
    private final RedissonClient redissonClient;

    @PostMapping("/list/page")
    public Result<Page<RecordResVO>> listPage(@RequestBody @Validated RecordPageDTO recordPageDTO) {
        String goodsName = recordPageDTO.getGoodsName();
        Integer storage = recordPageDTO.getStorage();
        Integer goodsType = recordPageDTO.getGoodsType();
        Integer currentUserId = recordPageDTO.getUserId();
        Integer roleId = recordPageDTO.getRoleId();

        // 如果前端输入了物品名、仓库、分类，需要去goods表查
        List<Integer> targetGoodsIds = null;
        if (StringUtils.isNotBlank(goodsName) ||
                (storage != null && StringUtils.isNotBlank(storage.toString())) ||
                (goodsType != null && StringUtils.isNotBlank(goodsType.toString()))) {

            LambdaQueryWrapper<Goods> goodsWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(goodsName)) goodsWrapper.like(Goods::getName, goodsName);
            if (storage != null && StringUtils.isNotBlank(storage.toString())) goodsWrapper.eq(Goods::getStorage, storage);
            if (goodsType != null && StringUtils.isNotBlank(goodsType.toString())) goodsWrapper.eq(Goods::getGoodsType, goodsType);

            List<Goods> matchedGoods = goodsService.list(goodsWrapper);
            if (matchedGoods.isEmpty()) {
                return Result.success(new Page<>());
            }
            // 提取出所有符合条件的 goods_id
            targetGoodsIds = matchedGoods.stream().map(Goods::getId).collect(Collectors.toList());
        }

        // 单表极速查询 Record 表

        Page<Record> page = new Page<>(recordPageDTO.getPageNum(), recordPageDTO.getPageSize());
        LambdaQueryWrapper<Record> recordWrapper = new LambdaQueryWrapper<>();

        if (targetGoodsIds != null) {
            recordWrapper.in(Record::getGoods, targetGoodsIds);
        }

        if (roleId != null && roleId == 2 && currentUserId != null) {

            recordWrapper.eq(Record::getUserId, currentUserId);
        }

        // 按照操作时间倒序排列（最新出入库的在最前面）
        recordWrapper.orderByDesc(Record::getCreatetime);

        recordService.page(page, recordWrapper);
        List<Record> records = page.getRecords();
        if (records.isEmpty()) {
            return Result.success(new Page<>());
        }

        // 提取这一页流水里所有的外键ID (去重)
        Set<Integer> goodsIds = records.stream().map(Record::getGoods).collect(Collectors.toSet());
        Set<Integer> userIds = records.stream().map(Record::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Integer> adminIds = records.stream().map(Record::getAdminId).filter(Objects::nonNull).collect(Collectors.toSet());

        // 把取货人和操作人的 ID 混在一起，一次性去数据库查出来，减少 IO 次数
        Set<Integer> allPeopleIds = new HashSet<>(userIds);
        allPeopleIds.addAll(adminIds);

        // 批量调用底层 Service 获取字典数据，并转成 Map<ID, 实体> 方便后续高速查找
        Map<Integer, Goods> goodsMap = goodsIds.isEmpty() ? new HashMap<>() :
                goodsService.listByIds(goodsIds).stream().collect(Collectors.toMap(Goods::getId, g -> g));

        Map<Integer, User> userMap = allPeopleIds.isEmpty() ? new HashMap<>() :
                userService.listByIds(allPeopleIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        // 遍历拼接组装最终的 DTO
        List<RecordResVO> resList = records.stream().map(r -> {
            RecordResVO res = new RecordResVO();
            // 把 Record 里的基础属性拷贝给 res
            BeanUtils.copyProperties(r, res);
            // 组装物品信息
            Goods g = goodsMap.get(r.getGoods());
            if (g != null) {
                res.setGoodsName(g.getName());
                res.setStorage(g.getStorage());
                res.setGoodsType(g.getGoodsType());
            }
            // 组装人员信息
            User u = userMap.get(r.getUserId());
            if (u != null) res.setUserName(u.getName());

            User admin = userMap.get(r.getAdminId());
            if (admin != null) res.setAdminName(admin.getName());

            return res;
        }).collect(Collectors.toList());


        Page<RecordResVO> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(resList);

        return Result.success(resPage);
    }

    @PostMapping("/addRecord")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRecord(@RequestBody @Validated RecordAddDTO recordAddDTO) {
        Integer actionType = recordAddDTO.getActionType();
        Integer goodsId = recordAddDTO.getGoods();
        Integer ccount = recordAddDTO.getCount();

        if (goodsId == null ) {
            throw new BusinessException(400,"参数错误");
        }

        // 定义极其精细的锁粒度：只锁当前被操作的这件物品！
        String lockKey = "wms:lock:goods:" + goodsId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试拿锁 (Fail-Fast 快速失败机制)
            // 最多排队等 3 秒。-1 代表开启看门狗（业务没执行完绝对不释放锁）
            boolean isLocked = lock.tryLock(3, -1, TimeUnit.SECONDS);

            if (!isLocked) {
                // 如果 3 秒都没排到队，说明瞬间有成百上千人同时操作这个物品。
                // 触发快速失败，直接打回请求，保护底层的 MySQL
                throw new BusinessException(500, "当前操作人数过多，请稍后再试！");
            }

            Record record = new Record();
            BeanUtils.copyProperties(recordAddDTO, record);

            if (actionType.equals(1)) {
                boolean success = goodsService.update(new LambdaUpdateWrapper<Goods>()
                        .eq(Goods::getId, goodsId)
                        .setSql("count = count + " + ccount)
                );
                if (!success) {
                    throw new BusinessException(400,"入库失败，物品可能已被删除");
                }

            } else if (actionType.equals(2)) {
                // MySQL 乐观锁兜底：ge(count, ccount) 保证数据库层面的绝对安全
                boolean success = goodsService.update(new LambdaUpdateWrapper<Goods>()
                        .eq(Goods::getId, goodsId)
                        .ge(Goods::getCount, ccount)
                        .setSql("count = count - " + ccount)
                );
                if (!success) {
                    throw new BusinessException(400,"手慢了！当前库存不足");
                }
            } else {
                throw new BusinessException(400,"非法的操作类型");
            }

            record.setCreatetime(LocalDateTime.now());
            recordService.save(record);

            return Result.success();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(500, "系统繁忙，操作异常！");
        } finally {

            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
