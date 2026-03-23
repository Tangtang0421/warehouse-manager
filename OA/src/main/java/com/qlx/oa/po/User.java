package com.qlx.oa.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author qlx
 * @since 2026-03-10
 */
@Getter
@Setter
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    @NotBlank(message = "账号不可为空")
    private String no;

    /**
     * 名字
     */
    @NotBlank(message = "名字不可为空")
    private String name;

    /**
     * 密码
     */
    @NotBlank(message = "密码不可为空")
    private String password;

    @NotNull(message = "年龄不可为空")
    private Integer age;

    /**
     * 性别
     */
    @NotNull(message = "性别不可为空")
    private Integer sex;

    /**
     * 电话
     */
    @NotBlank(message = "电话不可为空")
    private String phone;

    /**
     * 角色 0超级管理员，1管理员，2普通账号
     */
    @NotNull(message = "角色不可为空")
    private Integer roleId;

    /**
     * 是否有效，1有效，0无效
     */
    @NotNull(message = "用户有效位不可为空")
    private Integer validStatus;
}
