package com.qlx.oa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAddDTO {

    @NotBlank(message = "账号不可为空")
    private String no;

    @NotBlank(message = "名字不可为空")
    private String name;

    @NotBlank(message = "密码不可为空")
    private String password;

    @NotNull(message = "年龄不可为空")
    private Integer age;

    @NotNull(message = "性别不可为空")
    private Integer sex;

    @NotBlank(message = "电话不可为空")
    private String phone;
    private Integer roleId;
    private Integer validStatus = 1;



}
