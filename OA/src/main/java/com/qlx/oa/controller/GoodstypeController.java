package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.QueryPageParam;
import com.qlx.oa.common.Result;
import com.qlx.oa.entity.Goodstype;
import com.qlx.oa.service.IGoodstypeService;
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
@RequestMapping("/goodstype")
public class GoodstypeController {

    @Autowired
    private IGoodstypeService goodstypeService;

    @GetMapping("/list")
    public Result<List<Goodstype>> list(){
        return Result.success(goodstypeService.list());
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id){
        boolean flag = goodstypeService.removeById(id);
        return flag ? Result.success() : Result.error(400, "物品分类删除失败");
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Goodstype goodstype){
        String name = goodstype.getName();
        if(StringUtils.isBlank(name)){
            return Result.error(400, "分类名称不可为空");
        }
        LambdaQueryWrapper<Goodstype> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goodstype::getName, name);
        if(goodstypeService.count(wrapper) > 0){
            return Result.error(400, "该物品分类已存在，请勿重复添加");
        }
        boolean flag = goodstypeService.save(goodstype);
        return flag ? Result.success() : Result.error(500, "物品分类添加失败");
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Goodstype goodstype){
        if(goodstype.getId() == null){
            return Result.error(400, "物品分类ID不可为空");
        }
        if(StringUtils.isBlank(goodstype.getName())){
            return Result.error(400, "分类名称不可为空");
        }

        boolean flag = goodstypeService.updateById(goodstype);
        return flag ? Result.success() : Result.error(500, "物品分类修改失败");
    }

    @PostMapping("/list/page")
    public Result<Page<Goodstype>> listPage(@RequestBody QueryPageParam queryPageParam){
        Page<Goodstype> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        String keyword = (String) queryPageParam.getParam().get("keyword");

        LambdaQueryWrapper<Goodstype> wrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(keyword)){

            wrapper.and(w -> w.like(Goodstype::getName, keyword)
                    .or()
                    .like(Goodstype::getRemark, keyword));
        }

        goodstypeService.page(page, wrapper);
        return Result.success(page);
    }
}
