package com.qlx.oa.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
@Getter
@Setter
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单编码
     */
    @TableField("menuCode")
    private String menuCode;

    /**
     * 菜单名字
     */
    @TableField("menuName")
    private String menuName;

    /**
     * 菜单级别
     */
    @TableField("menuLevel")
    private String menuLevel;

    /**
     * 菜单的父code
     */
    @TableField("menuParentCode")
    private String menuParentCode;

    /**
     * 点击触发的函数
     */
    @TableField("menuClick")
    private String menuClick;

    /**
     * 权限 0超级管理员, 1表示管理员, 2表示普通用户, 可以用逗号组合使用
     */
    @TableField("menuRight")
    private String menuRight;

    /**
     * 前端路由路径
     */
    @TableField("menuComponent")
    private String menuComponent;

    /**
     * 图标样式
     */
    @TableField("menuIcon")
    private String menuIcon;
}
