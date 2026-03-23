package com.qlx.oa.dto;

import lombok.Data;

@Data
public class GoodsUpdateDTO {
    private Integer id;
    private String name;
    private Integer storage;
    private Integer goodsType;
    private Integer count;
    private String remark;
}
