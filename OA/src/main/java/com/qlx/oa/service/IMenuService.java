package com.qlx.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qlx.oa.po.Menu;

import java.util.List;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
public interface IMenuService extends IService<Menu> {
    List<Menu> getMenusByRoleId(Integer roleId);

}
