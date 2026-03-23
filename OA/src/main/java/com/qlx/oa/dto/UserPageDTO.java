package com.qlx.oa.dto;

import lombok.Data;

@Data
public class UserPageDTO extends BasePageDTO{
    private String keyword;
    private Integer sex;
    private Integer roleId;

}
