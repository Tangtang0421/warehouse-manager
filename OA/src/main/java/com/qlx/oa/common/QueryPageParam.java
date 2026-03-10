package com.qlx.oa.common;

import lombok.Data;

import java.util.HashMap;

@Data
public class QueryPageParam {
    // 默认第一页
    private Integer pageNum = 1;
    // 默认每页 10 条
    private Integer pageSize = 10;

    // 接收前端传来的除了页码之外的所有查询条件
    // 比如：{"pageNum": 1, "pageSize": 10, "param": {"keyword": "张三", "roleId": 1}}
    private HashMap<String, Object> param = new HashMap<>();
}
