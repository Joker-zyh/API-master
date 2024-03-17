package com.heng.project.model.vo;

import com.heng.hengapicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     * 是否已点赞
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}