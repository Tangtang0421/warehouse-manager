package com.qlx.oa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoodstypeAddDTO {
    @NotBlank(message = "物品分类不可为空")
    private String name;
    private String remark;
}
