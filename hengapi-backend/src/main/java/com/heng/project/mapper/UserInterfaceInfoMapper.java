package com.heng.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author 86191
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2024-03-07 21:55:28
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInterfaceInfo(int limit);
}




