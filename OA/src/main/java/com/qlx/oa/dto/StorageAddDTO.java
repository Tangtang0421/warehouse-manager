package com.qlx.oa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StorageAddDTO {
    @NotBlank(message = "仓库名不可为空")
    private String name;
    private String remark;
}
