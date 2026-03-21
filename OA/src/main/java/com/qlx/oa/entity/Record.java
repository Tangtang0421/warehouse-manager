package com.qlx.oa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private Integer goods;

    /**
     * 取货人/补货人
     */
    @TableField("userId")
    private Integer userId;

    /**
     * 操作人id
     */
    @TableField("admin_id")
    private Integer adminId;

    /**
     * 数量
     */
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
