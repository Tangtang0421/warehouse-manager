package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.QueryPageParam;
import com.qlx.oa.common.Result;
import com.qlx.oa.entity.Storage;
import com.qlx.oa.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<Boolean> addStorage(@RequestBody Storage storage){
        String name = storage.getName();
        if(StringUtils.isBlank(name)){
            return Result.error(500, "仓库名不可为空");
        }
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Storage::getName, name);
        if(storageService.count(wrapper) > 0){
            return Result.error(500, "仓库已存在");
        }

        boolean flag = storageService.save(storage);
        return flag ? Result.success() : Result.error(500, "新增仓库失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStorage(@PathVariable Integer id){
        boolean flag = storageService.removeById(id);
        return flag ? Result.success() : Result.error(400, "删除仓库失败，仓库可能不存在");
    }

    @PostMapping("/update")
    public Result<Boolean> updateStorage(@RequestBody Storage storage){
        Integer id = storage.getId();
        if(id == null){
            return Result.error(400, "仓库ID不可为空");
        }
        if(StringUtils.isBlank(storage.getName())){
            return Result.error(400, "仓库名不可为空");
        }
        boolean flag = storageService.updateById(storage);
        return flag ? Result.success() : Result.error(400, "更新仓库失败，仓库可能不存在");
    }

    @GetMapping("/list")
    public Result<List<Storage>> listStorage(){
        List<Storage> list = storageService.list();
        return Result.success(list);
    }

    @PostMapping("/list/page")
    public Result<Page<Storage>> listStorageByPage(@RequestBody QueryPageParam queryPageParam){
        Page<Storage> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        String keyword = (String) queryPageParam.getParam().get("keyword");
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(keyword)){
            wrapper.and(w -> w.like(Storage::getName, keyword)
                    .or()
                    .like(Storage::getRemark, keyword));
        }

        storageService.page(page, wrapper);
        return Result.success(page);
    }
}
