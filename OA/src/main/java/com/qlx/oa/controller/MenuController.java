package com.qlx.oa.controller;

import com.qlx.oa.common.Result;
import com.qlx.oa.po.Menu;
import com.qlx.oa.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private IMenuService menuService;
    @GetMapping("/list")
    public Result<List<Menu>> getMenuList(@RequestParam String roleId){
        List<Menu> list = menuService.lambdaQuery().like(Menu::getMenuRight,roleId).list();
        return Result.success(list);
    }

}
