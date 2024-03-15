package com.heng.hengapicommon.service;

import com.heng.hengapicommon.model.entity.UserInterfaceInfo;

/**
* @author 86191
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-03-07 21:55:28
*/
public interface InnerUserInterfaceInfoService {


    /**
     * 更新接口调用次数
     * @param interfaceInfoId
     * @param userId
     */
    boolean invokeCount(Long interfaceInfoId, Long userId);

    UserInterfaceInfo getUserInterfaceInfoById(Long userId, Long interfaceInfoId);
}
