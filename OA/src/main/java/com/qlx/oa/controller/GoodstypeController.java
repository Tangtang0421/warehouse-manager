package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import com.qlx.oa.dto.GoodstypeAddDTO;
import com.qlx.oa.dto.GoodstypePageDTO;
import com.qlx.oa.dto.GoodstypeUpdateDTO;
import com.qlx.oa.po.Goodstype;
import com.qlx.oa.service.IGoodstypeService;
import com.qlx.oa.vo.GoodstypeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@RequestMapping("/goodstype")
public class GoodstypeController {

    @Autowired
    private IGoodstypeService goodstypeService;

    @GetMapping("/list")
    @Cacheable(value = "wms:goodstype", key = "'list'")
    public Result<List<GoodstypeVO>> list(){
        List<Goodstype> list = goodstypeService.list();
        List<GoodstypeVO> VOlist = list.stream().map(goodstype -> {
            GoodstypeVO goodstypeVO = new GoodstypeVO();
            BeanUtils.copyProperties(goodstype,goodstypeVO);
            return goodstypeVO;
        }).collect(Collectors.toList());
        return Result.success(VOlist);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "wms:goodstype", key = "'list'")
    public Result<Boolean> delete(@PathVariable Integer id){
        boolean flag = goodstypeService.removeById(id);
        if(!flag){
            throw new BusinessException(400,"物品分类删除失败");
        }
        return Result.success();
    }

    @PostMapping("/add")
    @CacheEvict(value = "wms:goodstype", key = "'list'")
    public Result<Boolean> add(@RequestBody @Validated GoodstypeAddDTO goodstypeAddDTO){

        LambdaQueryWrapper<Goodstype> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goodstype::getName, goodstypeAddDTO.getName());
        if(goodstypeService.count(wrapper) > 0){
            throw new BusinessException(400, "该物品分类已存在，请勿重复添加");
        }
        Goodstype goodstype = new Goodstype();
        BeanUtils.copyProperties(goodstypeAddDTO,goodstype);
        boolean flag = goodstypeService.save(goodstype);
        if(!flag){
            throw new BusinessException("物品分类添加失败");
        }
        return Result.success() ;
    }

    @PostMapping("/update")
    @CacheEvict(value = "wms:goodstype", key = "'list'")
    public Result<Boolean> update(@RequestBody @Validated GoodstypeUpdateDTO goodstypeUpdateDTO){
        if(goodstypeUpdateDTO.getId() == null){
            throw  new BusinessException(400, "物品分类ID不可为空");
        }
        Goodstype goodstype = new Goodstype();
        BeanUtils.copyProperties(goodstypeUpdateDTO,goodstype);
        boolean flag = goodstypeService.updateById(goodstype);
        if(!flag){
            throw new BusinessException("物品分类修改失败");
        }
        return Result.success();
    }

    @PostMapping("/list/page")
    public Result<Page<GoodstypeVO>> listPage(@RequestBody @Validated GoodstypePageDTO goodstypePageDTO){
        Page<Goodstype> page = new Page<>(goodstypePageDTO.getPageNum(), goodstypePageDTO.getPageSize());
        LambdaQueryWrapper<Goodstype> wrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(goodstypePageDTO.getKeyword())){

            wrapper.and(w -> w.like(Goodstype::getName, goodstypePageDTO.getKeyword())
                    .or()
                    .like(Goodstype::getRemark, goodstypePageDTO.getKeyword()));
        }

        goodstypeService.page(page, wrapper);
        Page<GoodstypeVO> VOpage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<GoodstypeVO> list= page.getRecords().stream().map(goodstype -> {
            GoodstypeVO goodstypeVO = new GoodstypeVO();
            BeanUtils.copyProperties(goodstype,goodstypeVO);
            return goodstypeVO;
        }).collect(Collectors.toList());
        VOpage.setRecords(list);
        return Result.success(VOpage);
    }
}
