package com.qlx.oa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordRes extends Record {
    private String goodsName;
    private String userName;
    private String adminName;

    private Integer storage;
    private Integer goodsType;
}
