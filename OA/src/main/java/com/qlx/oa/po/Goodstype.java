package com.qlx.oa.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author qlx
 * @since 2026-03-16
 */
@Getter
@Setter
public class Goodstype implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名
     */
    @NotBlank(message = "物品分类名不可为空")
    private String name;

    /**
     * 备注
     */
    private String remark;
}
