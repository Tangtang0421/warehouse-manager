package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import com.qlx.oa.dto.GoodsAddDTO;
import com.qlx.oa.dto.GoodsPageDTO;
import com.qlx.oa.dto.GoodsUpdateDTO;
import com.qlx.oa.po.Goods;
import com.qlx.oa.service.IGoodsService;
import com.qlx.oa.vo.GoodsVO;
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
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @GetMapping("/list")
    public Result<List<GoodsVO>> listGoods(){
        List<Goods> list = goodsService.list();
        List<GoodsVO> goodsVOList = list.stream().map(goods -> {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(goods, goodsVO);
            return goodsVO;
        }).collect(Collectors.toList());
        return Result.success(goodsVOList);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteGoods(@PathVariable Integer id){
        boolean flag = goodsService.removeById(id);
        if(!flag){
            throw new BusinessException(400,"物品删除失败");
        }
        return Result.success();
    }

    @PostMapping("/add")
    public Result<Boolean> addGoods(@RequestBody @Validated GoodsAddDTO  goodsAddDTO){

        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getName, goodsAddDTO.getName()).eq(Goods::getStorage, goodsAddDTO.getStorage());

        if(goodsService.count(wrapper) > 0){
            throw new BusinessException(400, "该仓库中已存在同名物品");
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsAddDTO,goods);
        boolean flag = goodsService.save(goods);
        if(!flag){
            throw new BusinessException(500,"物品添加失败");
        }
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Boolean> updateGoods(@RequestBody @Validated GoodsUpdateDTO goodsUpdateDTO){
        if(goodsUpdateDTO.getId() == null){
            throw new BusinessException(400, "该物品不存在");
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsUpdateDTO,goods);
        boolean flag = goodsService.updateById(goods);
        if(!flag){
            throw new BusinessException(500,"物品修改失败");
        }
        return Result.success() ;
    }

    @PostMapping("/list/page")
    public Result<Page<GoodsVO>> listPage(@RequestBody @Validated GoodsPageDTO goodsPageDTO){
        Page<Goods> page = new Page<>(goodsPageDTO.getPageNum(), goodsPageDTO.getPageSize());
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();


        if(StringUtils.isNotBlank(goodsPageDTO.getKeyword())){
            wrapper.and(w -> w.like(Goods::getName, goodsPageDTO.getKeyword()).or().like(Goods::getRemark, goodsPageDTO.getKeyword()));
        }


        if(goodsPageDTO.getStorage() != null && StringUtils.isNotBlank(goodsPageDTO.getStorage().toString())){
            wrapper.eq(Goods::getStorage, goodsPageDTO.getStorage());
        }

        if(goodsPageDTO.getGoodsType() != null && StringUtils.isNotBlank(goodsPageDTO.getGoodsType().toString())){
            wrapper.eq(Goods::getGoodsType, goodsPageDTO.getGoodsType());
        }

        goodsService.page(page, wrapper);
        List<GoodsVO>list =page.getRecords().stream().map(goods -> {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(goods,goodsVO);
            return goodsVO;
        }).collect(Collectors.toList());
        Page<GoodsVO> goodsVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        goodsVOPage.setRecords(list);
        return Result.success(goodsVOPage);
    }
}
