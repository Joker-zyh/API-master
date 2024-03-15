package com.heng.hengapicommon.service;


import com.heng.hengapicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService{

    /**
     * 根据accessKey获取用户
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
