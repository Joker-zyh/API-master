package com.heng.hengapiorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heng.hengapicommon.model.entity.Order;
import com.heng.hengapicommon.model.vo.OrderVO;
import com.heng.hengapiorder.dto.OrderAddRequest;
import com.heng.hengapiorder.dto.OrderQueryRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86191
* @description 针对表【order(订单表)】的数据库操作Service
* @createDate 2024-03-17 15:34:03
*/
public interface OrderService extends IService<Order> {

    /**
     * 添加订单
     * @param orderAddRequest
     * @param request
     * @return
     */
    OrderVO addOrder(OrderAddRequest orderAddRequest, HttpServletRequest request);

    /**
     * 获取我的订单
     * @param orderQueryRequest
     * @param request
     * @return
     */
    Page<OrderVO> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request);

    /**
     * 获取前 limit 购买数量的接口
     * @param limit
     * @return
     */
    List<Order> listTopBuyInterfaceInfo(int limit);
}
