package com.qlx.oa.dto;

import lombok.Data;

@Data
public class BasePageDTO {
    // 默认第一页
    private Integer pageNum = 1;
    // 默认每页 10 条
    private Integer pageSize = 10;
}
