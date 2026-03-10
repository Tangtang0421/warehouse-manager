package com.qlx.oa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`user`")
public class User {

    @TableId(type = IdType.AUTO)//id为主键
    private Integer id;

    private String no;       // 账号
    private String name;     // 名字
    private String password; // 密码
    private Integer age;     // 年龄
    private Integer sex;     // 性别
    private String phone;    // 电话

    // 数据库里是 role_id，Java 里写成驼峰命名 roleId，MP可以自动映射
    private Integer roleId;

    @TableField("isValid") // 数据库里直接拼成了 isValid，加个注解强行绑定，防止 MP 找不到
    private String isValid;
}

