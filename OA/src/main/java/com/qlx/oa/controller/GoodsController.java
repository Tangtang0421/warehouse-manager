package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.QueryPageParam;
import com.qlx.oa.common.Result;
import com.qlx.oa.entity.Goods;
import com.qlx.oa.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @GetMapping("/list")
    public Result<List<Goods>> listGoods(){
        return Result.success(goodsService.list());
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteGoods(@PathVariable Integer id){
        boolean flag = goodsService.removeById(id);
        return flag ? Result.success() : Result.error(500, "删除物品失败");
    }

    @PostMapping("/add")
    public Result<Boolean> addGoods(@RequestBody Goods goods){
        String name = goods.getName();
        Integer storageId = goods.getStorage();
        Integer goodsTypeId = goods.getGoodsType();
        Integer count = goods.getCount();

        if(StringUtils.isBlank(name) || storageId == null || goodsTypeId == null || count == null){
            return Result.error(400, "物品核心信息（名称、仓库、分类、数量）不可为空");
        }

        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getName, name).eq(Goods::getStorage, storageId);

        if(goodsService.count(wrapper) > 0){
            return Result.error(400, "该仓库中已存在同名物品");
        }

        boolean flag = goodsService.save(goods);
        return flag ? Result.success() : Result.error(500, "物品添加失败");
    }

    @PostMapping("/update")
    public Result<Boolean> updateGoods(@RequestBody Goods goods){
        if(goods.getId() == null){
            return Result.error(400, "该物品不存在");
        }
        boolean flag = goodsService.updateById(goods);
        return flag ? Result.success() : Result.error(500, "物品修改失败");
    }

    @PostMapping("/list/page")
    public Result<Page<Goods>> listPage(@RequestBody QueryPageParam param){
        Page<Goods> page = new Page<>(param.getPageNum(), param.getPageSize());
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();

        String keyword = (String) param.getParam().get("keyword");
        Object storageObj = param.getParam().get("storage");
        Object goodsTypeObj = param.getParam().get("goodsType");

        if(StringUtils.isNotBlank(keyword)){
            wrapper.and(w -> w.like(Goods::getName, keyword).or().like(Goods::getRemark, keyword));
        }


        if(storageObj != null && StringUtils.isNotBlank(storageObj.toString())){
            wrapper.eq(Goods::getStorage, storageObj);
        }

        if(goodsTypeObj != null && StringUtils.isNotBlank(goodsTypeObj.toString())){
            wrapper.eq(Goods::getGoodsType, goodsTypeObj);
        }

        goodsService.page(page, wrapper);
        return Result.success(page);
    }
}
