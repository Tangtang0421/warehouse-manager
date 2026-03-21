package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.QueryPageParam;
import com.qlx.oa.common.Result;
import com.qlx.oa.entity.Goods;
import com.qlx.oa.entity.Record;
import com.qlx.oa.entity.RecordRes;
import com.qlx.oa.entity.User;
import com.qlx.oa.service.IGoodsService;
import com.qlx.oa.service.IRecordService;
import com.qlx.oa.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
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

    @PostMapping("/list/page")
    public Result<Page<RecordRes>> listPage(@RequestBody QueryPageParam queryPageParam) {
        String goodsName = (String) queryPageParam.getParam().get("goodsName");
        Object storageObj = queryPageParam.getParam().get("storage");
        Object goodsTypeObj = queryPageParam.getParam().get("goodsType");
        Integer currentUserId = (Integer) queryPageParam.getParam().get("userId");
        Integer roleId = (Integer) queryPageParam.getParam().get("roleId");

        // 如果前端输入了物品名、仓库、分类，需要去goods表查
        List<Integer> targetGoodsIds = null;
        if (StringUtils.isNotBlank(goodsName) ||
                (storageObj != null && StringUtils.isNotBlank(storageObj.toString())) ||
                (goodsTypeObj != null && StringUtils.isNotBlank(goodsTypeObj.toString()))) {

            LambdaQueryWrapper<Goods> goodsWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(goodsName)) goodsWrapper.like(Goods::getName, goodsName);
            if (storageObj != null && StringUtils.isNotBlank(storageObj.toString())) goodsWrapper.eq(Goods::getStorage, storageObj);
            if (goodsTypeObj != null && StringUtils.isNotBlank(goodsTypeObj.toString())) goodsWrapper.eq(Goods::getGoodsType, goodsTypeObj);

            List<Goods> matchedGoods = goodsService.list(goodsWrapper);
            if (matchedGoods.isEmpty()) {
                return Result.success(new Page<>());
            }
            // 提取出所有符合条件的 goods_id
            targetGoodsIds = matchedGoods.stream().map(Goods::getId).collect(Collectors.toList());
        }

        // 单表极速查询 Record 表

        Page<Record> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
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
        List<RecordRes> resList = records.stream().map(r -> {
            RecordRes res = new RecordRes();
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


        Page<RecordRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(resList);

        return Result.success(resPage);
    }

    @PostMapping("/addRecord")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRecord(@RequestBody Record record) {
        Integer actionType = record.getActionType();
        Integer goodsId = record.getGoods();
        Integer ccount = record.getCount();

        if (goodsId == null || ccount == null || ccount <= 0 || actionType == null) {
            return Result.error(400,"参数错误");
        }

        if (actionType.equals(1)) {

            boolean success = goodsService.update(new LambdaUpdateWrapper<Goods>()
                    .eq(Goods::getId, goodsId)
                    .setSql("count = count + " + ccount)
            );
            if (!success) {
                throw new RuntimeException("入库失败，物品可能已被删除");
            }

        } else if (actionType.equals(2)) {

            boolean success = goodsService.update(new LambdaUpdateWrapper<Goods>()
                    .eq(Goods::getId, goodsId)
                    .ge(Goods::getCount, ccount)
                    .setSql("count = count - " + ccount)
            );
            if (!success) {
                throw new RuntimeException("手慢了！当前库存不足");
            }
        } else {
            return Result.error(400,"非法的操作类型");
        }

        record.setCreatetime(LocalDateTime.now());

        recordService.save(record);

        return Result.success();
    }

}
