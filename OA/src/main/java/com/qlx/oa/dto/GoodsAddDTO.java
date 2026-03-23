package com.qlx.oa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoodsAddDTO {
    @NotBlank(message = "物品名称不可为空")
    private String name;
    @NotNull(message = "所属仓库不可为空")
    private Integer storage;
    @NotNull(message = "物品分类不可为空")
    private Integer goodsType;

    @NotNull(message = "物品数量不可为空")
    @Min(value = 0,message = "物品数不可小于0")
    private Integer count;
    private String remark;
}
