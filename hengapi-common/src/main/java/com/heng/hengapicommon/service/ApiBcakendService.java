package com.heng.hengapicommon.service;


import com.heng.hengapicommon.model.entity.InterfaceInfo;
import com.heng.hengapicommon.model.entity.User;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

public interface ApiBcakendService {

    /**
     * 根据路径和方法类型获取接口信息
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 更新接口调用次数
     * @param interfaceInfoId
     * @param userId
     */
    boolean invokeCount(Long interfaceInfoId, Long userId);

    /**
     * 根据id获取用户接口信息
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    UserInterfaceInfo getUserInterfaceInfoById(Long userId, Long interfaceInfoId);


    /**
     * 根据accessKey获取用户
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

    /**
     * 根据id获取接口信息
     * @param id
     * @return
     */
    InterfaceInfo getInterfaceById(Long id);

    /**
     * 根据id获取系统剩余接口调用次数
     * @param id
     * @return
     */
    int getInterfaceStockById(Long id);

    /**
     * 扣除系统接口剩余调用次数
     * @param interfaceId
     * @param count
     * @return
     */
    boolean updateInterfaceStock(Long interfaceId, int count);

    /**
     * 恢复系统接口剩余调用次数
     * @param interfaceId
     * @param count
     * @return
     */
    boolean recoverInterfaceStock(Long interfaceId, int count);

    /**
     * 给用户分配接口
     * @param userId
     * @param interfaceId
     * @param count
     * @return
     */
    boolean updateUserInterfaceInvokeCount(Long userId, Long interfaceId, Integer count);


}
