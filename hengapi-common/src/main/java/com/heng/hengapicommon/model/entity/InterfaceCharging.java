package com.heng.hengapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口计费
 * @TableName interface_charging
 */
@TableName(value ="interface_charging")
@Data
public class InterfaceCharging implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 用户名
     */
    private Double charging;

    /**
     * 接口剩余可调用次数
     */
    private String availablePieces;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer is_deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}