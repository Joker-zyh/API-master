package com.heng.project.service.innerserviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heng.hengapicommon.model.entity.User;
import com.heng.hengapicommon.service.InnerUserService;
import com.heng.project.common.ErrorCode;
import com.heng.project.exception.BusinessException;
import com.heng.project.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
