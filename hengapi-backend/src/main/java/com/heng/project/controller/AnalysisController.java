package com.heng.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.heng.hengapicommon.model.entity.InterfaceInfo;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;
import com.heng.project.annotation.AuthCheck;
import com.heng.project.common.BaseResponse;
import com.heng.project.common.ErrorCode;
import com.heng.project.common.ResultUtils;
import com.heng.project.constant.UserConstant;
import com.heng.project.exception.BusinessException;
import com.heng.project.mapper.UserInterfaceInfoMapper;
import com.heng.project.model.vo.InterfaceInfoVO;
import com.heng.project.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoVO>> listInvokeInterfaceInfo(){
        List<UserInterfaceInfo> userInterfaceList = userInterfaceInfoMapper.listTopInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> collect = userInterfaceList.stream().
                collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        //查询对应接口信息
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",collect.keySet());
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        //集合判空
        if (CollectionUtils.isEmpty(interfaceInfoList)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //封装结果集VO
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            interfaceInfoVO.setTotalNum(collect.get(interfaceInfo.getId()).get(0).getTotalNum());
            return interfaceInfoVO;

        }).collect(Collectors.toList());

        return ResultUtils.success(interfaceInfoVOList);
    }
}
