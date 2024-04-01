package com.heng.hengapiorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heng.hengapicommon.model.entity.Order;

import java.util.List;


/**
* @author 86191
* @description 针对表【order(订单表)】的数据库操作Mapper
* @createDate 2024-03-17 15:34:03
* @Entity generator.domain.Order
*/
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 获取前 limit 购买数量的接口
     * @param limit
     * @return
     */
    List<Order> listTopBuyInterfaceInfo(int limit);
}




