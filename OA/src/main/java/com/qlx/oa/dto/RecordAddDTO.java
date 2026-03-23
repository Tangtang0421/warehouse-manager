package com.qlx.oa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordAddDTO {
    @NotNull(message = "操作类型不可为空")
    private Integer actionType;
    @NotNull(message = "物品不可为空")
    private Integer goods;
    @NotNull(message = "数量不可为空")
    @Min(value = 1,message = "操作物品数不可小于1")
    private Integer count;
    private String remark;
    @NotNull(message = "取货/补货人不可为空")
    private Integer userId;

    @NotNull(message = "操作人不可为空")
    private Integer adminId;
}
