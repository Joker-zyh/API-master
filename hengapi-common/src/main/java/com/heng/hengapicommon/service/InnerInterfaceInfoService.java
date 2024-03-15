package com.heng.hengapicommon.service;


import com.heng.hengapicommon.model.entity.InterfaceInfo;

/**
* @author 86191
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-02-27 11:24:36
*/
public interface InnerInterfaceInfoService{

    /**
     * 根据路径和方法类型获取接口信息
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
