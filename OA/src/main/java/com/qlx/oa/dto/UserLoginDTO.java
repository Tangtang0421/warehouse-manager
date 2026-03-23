package com.qlx.oa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "账号不可为空")
    private String no;
    @NotBlank(message = "密码不可为空")
    private String password;
}
