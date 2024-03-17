package com.heng.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;

/**
* @author 86191
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-03-07 21:55:28
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add 是否为创建校验
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 更新接口调用次数
     * @param interfaceInfoId
     * @param userId
     */
    boolean invokeCount(Long interfaceInfoId, Long userId);
}
