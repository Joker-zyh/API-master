package com.heng.project.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heng.hengapicommon.model.entity.InterfaceCharging;
import com.heng.hengapicommon.model.entity.InterfaceInfo;
import com.heng.hengapicommon.model.entity.User;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;
import com.heng.hengapicommon.service.ApiBcakendService;
import com.heng.project.common.ErrorCode;
import com.heng.project.exception.BusinessException;
import com.heng.project.mapper.InterfaceChargingMapper;
import com.heng.project.mapper.InterfaceInfoMapper;
import com.heng.project.mapper.UserMapper;
import com.heng.project.service.InterfaceChargingService;
import com.heng.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class ApiBackendServiceImpl implements ApiBcakendService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private InterfaceChargingService interfaceChargingService;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StringUtils.isAnyBlank(path, method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",path);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean invokeCount(Long interfaceInfoId, Long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfoById(Long userId, Long interfaceInfoId) {
        if (interfaceInfoId<= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户或接口不存在");
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("interfaceInfoId",interfaceInfoId);
        return userInterfaceInfoService.getOne(queryWrapper);
    }

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据id获取接口信息
     *
     * @param id
     * @return
     */
    @Override
    public InterfaceInfo getInterfaceById(Long id) {
        return interfaceInfoMapper.selectById(id);
    }

    /**
     * 根据id获取系统剩余接口调用次数
     * @param id
     * @return
     */
    @Override
    public int getInterfaceStockById(Long id) {
        QueryWrapper<InterfaceCharging> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceId",id);
        InterfaceCharging interfaceCharging = interfaceChargingService.getOne(queryWrapper);
        if (interfaceCharging == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口不存在");
        }
        return Integer.parseInt(interfaceCharging.getAvailablePieces());
    }

    /**
     * 扣除系统接口剩余调用次数
     *
     * @param interfaceId
     * @param count
     * @return
     */
    @Override
    public boolean updateInterfaceStock(Long interfaceId, int count) {
        UpdateWrapper<InterfaceCharging> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("availablePieces = availablePieces -" + count)
                .eq("interfaceId", interfaceId).gt("availablePieces",count);
        return interfaceChargingService.update(updateWrapper);


    }

    /**
     * 恢复系统接口剩余调用次数
     *
     * @param interfaceId
     * @param count
     * @return
     */
    @Override
    public boolean recoverInterfaceStock(Long interfaceId, int count) {
        UpdateWrapper<InterfaceCharging> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("availablePieces = availablePieces +" + count)
                .eq("interfaceId", interfaceId);
        return interfaceChargingService.update(updateWrapper);
    }

    /**
     * 给用户分配接口
     *
     * @param userId
     * @param interfaceId
     * @param count
     * @return
     */
    @Override
    public boolean updateUserInterfaceInvokeCount(Long userId, Long interfaceId, Integer count) {
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("leftNum", count)
                .eq("userId", userId)
                .eq("interfaceId", interfaceId);
        return userInterfaceInfoService.update(updateWrapper);
    }
}
