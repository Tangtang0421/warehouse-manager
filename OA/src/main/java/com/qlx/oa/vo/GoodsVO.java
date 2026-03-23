package com.qlx.oa.vo;

import lombok.Data;

@Data
public class GoodsVO {
    private Integer id;
    private String name;
    private Integer storage;
    private Integer goodsType;
    private Integer count;
    private String remark;
}
