package com.qlx.oa.dto;

import lombok.Data;

@Data
public class RecordPageDTO extends BasePageDTO {
    private String goodsName;
    private Integer storage;
    private Integer goodsType;
    private Integer userId;
    private Integer roleId;
}
