package com.qlx.oa.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 货品id
     */
    @NotNull(message = "物品不可为空")
    private Integer goods;

    /**
     * 取货人/补货人
     */
    @TableField("userId")
    @NotNull(message = "取货/补货人不可为空")
    private Integer userId;

    /**
     * 操作人id
     */
    @TableField("admin_id")
    @NotNull(message = "操作人不可为空")
    private Integer adminId;

    /**
     * 数量
     */
    @NotNull
    @Min(value = 1,message = "操作物品数不可小于1")
    private Integer count;

    /**
     * 操作时间
     */

    private LocalDateTime createtime;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private Integer actionType;
}
