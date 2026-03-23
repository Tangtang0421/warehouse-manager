package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import com.qlx.oa.dto.StorageAddDTO;
import com.qlx.oa.dto.StoragePageDTO;
import com.qlx.oa.dto.StorageUpdateDTO;
import com.qlx.oa.po.Storage;
import com.qlx.oa.service.IStorageService;
import com.qlx.oa.vo.StorageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private IStorageService storageService;

    @PostMapping("/add")
    public Result<Boolean> addStorage(@RequestBody @Validated StorageAddDTO storageAddDTO){
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Storage::getName, storageAddDTO.getName());
        if(storageService.count(wrapper) > 0){
            throw new BusinessException(500, "仓库已存在");
        }
        Storage storage = new Storage();
        BeanUtils.copyProperties(storageAddDTO,storage);
        boolean flag = storageService.save(storage);
        if(!flag){
            throw new BusinessException(500,"新增仓库失败");
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStorage(@PathVariable Integer id){
        boolean flag = storageService.removeById(id);
        if(!flag){
            throw new BusinessException(400,"删除仓库失败，仓库可能不存在");
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Boolean> updateStorage(@RequestBody @Validated StorageUpdateDTO storageUpdateDTO){
        if(storageUpdateDTO.getId() == null){
            throw  new BusinessException(400, "仓库ID不可为空");
        }
        Storage storage = new Storage();
        BeanUtils.copyProperties(storageUpdateDTO,storage);
        boolean flag = storageService.updateById(storage);
        if(!flag){
            throw new BusinessException("更新仓库失败，仓库可能不存在");
        }
        return Result.success() ;
    }

    @GetMapping("/list")
    public Result<List<StorageVO>> listStorage(){
        List<Storage> list = storageService.list();
        List<StorageVO> VOList = list.stream().map(storage -> {
            StorageVO storageVO = new StorageVO();
            BeanUtils.copyProperties(storage,storageVO);
            return storageVO;
        }).collect(Collectors.toList());
        return Result.success(VOList);
    }

    @PostMapping("/list/page")
    public Result<Page<StorageVO>> listStorageByPage(@RequestBody @Validated StoragePageDTO storagePageDTO){
        Page<Storage> page = new Page<>(storagePageDTO.getPageNum(), storagePageDTO.getPageSize());
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(storagePageDTO.getKeyword())){
            wrapper.and(w -> w.like(Storage::getName, storagePageDTO.getKeyword())
                    .or()
                    .like(Storage::getRemark, storagePageDTO.getKeyword()));
        }

        storageService.page(page, wrapper);
        Page<StorageVO> VOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<StorageVO> list = page.getRecords().stream().map(storage -> {
            StorageVO storageVO = new StorageVO();
            BeanUtils.copyProperties(storage,storageVO);
            return storageVO;
        }).collect(Collectors.toList());
        VOPage.setRecords(list);
        return Result.success(VOPage);
    }
}
