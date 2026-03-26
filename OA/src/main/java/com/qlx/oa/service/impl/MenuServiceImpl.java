package com.qlx.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qlx.oa.mapper.MenuMapper;
import com.qlx.oa.po.Menu;
import com.qlx.oa.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Override
    public List<Menu> getMenusByRoleId(Integer roleId) {
        if (roleId == null) {
            return java.util.Collections.emptyList();
        }

        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(Menu::getMenuRight, String.valueOf(roleId));

        return this.list(wrapper);
    }

}
