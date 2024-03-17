package com.heng.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heng.hengapicommon.model.entity.InterfaceInfo;

/**
* @author 86191
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-27 11:24:36
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
