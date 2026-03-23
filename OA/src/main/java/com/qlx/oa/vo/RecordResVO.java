package com.qlx.oa.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordResVO {
    private Integer id;
    private Integer count;
    private String remark;
    private Integer actionType;
    private String goodsName;
    private String userName;
    private String adminName;
    private Integer storage;
    private Integer goodsType;
    private LocalDateTime createtime;
}
