package com.heng.hengapiorder.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heng.hengapicommon.common.BaseResponse;
import com.heng.hengapicommon.common.ResultUtils;
import com.heng.hengapicommon.model.vo.OrderVO;
import com.heng.hengapiorder.dto.OrderAddRequest;
import com.heng.hengapiorder.dto.OrderQueryRequest;
import com.heng.hengapiorder.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class OrderController {

    @Resource
    private OrderService orderService;


    @PostMapping("/addOrder")
    public BaseResponse<OrderVO> interfaceTOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request){
        OrderVO order = orderService.addOrder(orderAddRequest,request);
        return ResultUtils.success(order);
    }


    @GetMapping("/list")
    public BaseResponse<Page<OrderVO>> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request){
        Page<OrderVO> orderPage = orderService.listPageOrder(orderQueryRequest, request);
        return ResultUtils.success(orderPage);
    }


}
