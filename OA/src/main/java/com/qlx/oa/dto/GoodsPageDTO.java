package com.qlx.oa.dto;

import lombok.Data;

@Data
public class GoodsPageDTO extends BasePageDTO {
    private String keyword;
    private Integer storage;
    private Integer goodsType;
}
