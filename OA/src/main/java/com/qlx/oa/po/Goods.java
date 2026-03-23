package com.qlx.oa.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author qlx
 * @since 2026-03-17
 */
@Getter
@Setter
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 货名
     */
    @NotBlank(message = "物品名称不可为空")
    private String name;

    /**
     * 仓库
     */
    @NotNull(message = "所属仓库不可为空")
    private Integer storage;

    /**
     * 分类
     */
    @TableField("goodsType")
    @NotNull(message = "物品分类不可为空")
    private Integer goodsType;

    /**
     * 数量
     */
    @NotNull(message = "物品数量不可为空")
    @Min(value = 0,message = "物品数不可小于0")
    private Integer count;

    /**
     * 备注
     */
    private String remark;
}
