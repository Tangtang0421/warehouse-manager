package com.qlx.oa.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Integer id;
    private String no;
    private String name;
    private String password;
    private Integer age;
    private Integer sex;
    private String phone;
    private Integer roleId;
}
