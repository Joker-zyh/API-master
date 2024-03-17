package com.heng.project.service.innerserviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;
import com.heng.hengapicommon.service.InnerUserInterfaceInfoService;
import com.heng.project.common.ErrorCode;
import com.heng.project.exception.BusinessException;
import com.heng.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

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
}
