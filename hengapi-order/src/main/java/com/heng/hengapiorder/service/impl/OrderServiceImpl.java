package com.heng.hengapiorder.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.heng.hengapicommon.common.ErrorCode;
import com.heng.hengapicommon.common.JwtUtils;
import com.heng.hengapicommon.constant.UserConstant;
import com.heng.hengapicommon.exception.BusinessException;
import com.heng.hengapicommon.model.entity.InterfaceInfo;
import com.heng.hengapicommon.model.entity.User;
import com.heng.hengapicommon.model.vo.OrderVO;
import com.heng.hengapicommon.service.ApiBcakendService;
import com.heng.hengapiorder.dto.OrderAddRequest;
import com.heng.hengapiorder.dto.OrderQueryRequest;
import com.heng.hengapiorder.mapper.OrderMapper;
import com.heng.hengapiorder.service.OrderService;
import com.heng.hengapicommon.model.entity.Order;
import com.heng.hengapiorder.utils.OrderMqUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.heng.hengapiorder.config.RabbitMqConfig.*;

/**
* @author 86191
* @description 针对表【order(订单表)】的数据库操作Service实现
* @createDate 2024-03-17 15:34:03
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Gson gson;

    @Resource
    private ApiBcakendService apiBcakendService;

    @Resource
    private OrderMqUtils orderMqUtils;

    /**
     * 添加订单
     *
     * @param orderAddRequest
     * @param request
     * @return
     */
    @Transactional
    @Override
    public OrderVO addOrder(OrderAddRequest orderAddRequest, HttpServletRequest request) {
        //1.检验参数，如用户是否存在，接口是否存在等校验
        if (orderAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = orderAddRequest.getUserId();
        Long interfaceId = orderAddRequest.getInterfaceId();
        Double charging = orderAddRequest.getCharging();
        Integer count = orderAddRequest.getCount();
        BigDecimal totalAmount = orderAddRequest.getTotalAmount();

        if (userId == null || interfaceId == null || count ==null || totalAmount == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (count<=0 || totalAmount.compareTo(new BigDecimal(0)) < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //校验用户
        Long userIdByToken = JwtUtils.getUserIdByToken(request);
        if (userIdByToken == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String userString = stringRedisTemplate.opsForValue().get(UserConstant.USER_LOGIN_STATE + userIdByToken);
        User user = gson.fromJson(userString, User.class);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }

        //校验接口
        InterfaceInfo interfaceInfo = apiBcakendService.getInterfaceById(interfaceId);
        if (interfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口不存在");
        }

        //校验订单总价格
        double temp = charging * count;
        BigDecimal bd = new BigDecimal(temp);
        double finalAmount = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (finalAmount != totalAmount.doubleValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"订单价格错误");
        }

        //校验系统接口调用次数
        int interfaceStock = apiBcakendService.getInterfaceStockById(interfaceId);
        if (interfaceStock <= 0 || interfaceStock - count <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统接口剩余调用次数不足");
        }

        //2.扣除系统调用次数
        boolean updateInterfaceStockResult = apiBcakendService.updateInterfaceStock(interfaceId, count);
        if (!updateInterfaceStockResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"扣减库存失败");
        }

        //3.生成订单并保存至数据库
        Order order = new Order();
        order.setOrderSn(generateOrderNum(userId));
        order.setTotalAmount(totalAmount.doubleValue());
        BeanUtils.copyProperties(orderAddRequest, order);
        this.save(order);

        //4.消息队列发送延迟消息
        orderMqUtils.sendOrderSnInfo(order);

        //5.构造订单详情，并回显

        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        orderVO.setInterfaceId(interfaceId);
        orderVO.setOrderNumber(order.getOrderSn());

        orderVO.setTotal(Long.valueOf(count));
        orderVO.setCharging(charging);
        orderVO.setTotalAmount(totalAmount.doubleValue());

        orderVO.setStatus(order.getState());
        orderVO.setInterfaceName(interfaceInfo.getName());
        orderVO.setInterfaceDesc(interfaceInfo.getDescription());

        DateTime date = DateUtil.date();
        orderVO.setCreateTime(date);
        orderVO.setExpirationTime(DateUtil.offset(date, DateField.MINUTE, 30));

        return orderVO;
    }

    private String generateOrderNum(Long userId){
        String timeId = IdWorker.getTimeId();
        String substring = timeId.substring(0, timeId.length() - 15);
        return substring + RandomUtil.randomNumbers(5) + userId;

    }
    /**
     * 获取我的订单
     *
     * @param orderQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<OrderVO> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        return null;
    }

    /**
     * 获取前 limit 购买数量的接口
     *
     * @param limit
     * @return
     */
    @Override
    public List<Order> listTopBuyInterfaceInfo(int limit) {
        return null;
    }
}




