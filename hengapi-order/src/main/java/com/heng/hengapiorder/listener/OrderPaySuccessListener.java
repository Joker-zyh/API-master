package com.heng.hengapiorder.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heng.hengapicommon.common.ErrorCode;
import com.heng.hengapicommon.exception.BusinessException;
import com.heng.hengapicommon.model.entity.Order;
import com.heng.hengapicommon.service.ApiBcakendService;
import com.heng.hengapiorder.service.OrderService;
import com.rabbitmq.client.Channel;
import com.sun.org.apache.xpath.internal.operations.Or;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.heng.hengapicommon.constant.RabbitmqConstant.ORDER_SUCCESS_QUEUE_NAME;
import static com.heng.hengapicommon.constant.RedisConstant.SEND_ORDER_PAY_SUCCESS_INFO;

@Slf4j
@Component
public class OrderPaySuccessListener {

    /**
     * 1. 监听订单成功支付消息队列
     * 2. 接收到消息则根据传来的订单id修改订单状态，给用户分配接口调用次数。
     */

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderService orderService;

    @Resource
    private ApiBcakendService apiBcakendService;

    private static final String CONSUME_ORDER_PAY_SUCCESS_INFO = "rabbitmq:consume:order:paySuccess:message:";

    private static final Integer ORDER_PAY_SUCCESS_STATE = 1;

    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queuesToDeclare = {@Queue(ORDER_SUCCESS_QUEUE_NAME)})
    public void receiveOrderMsg(String tradeNo, Message message, Channel channel) throws IOException {
        //1.消息的可靠机制保障，如果消息成功被监听到说明消息已经成功由生产者将消息发送到队列中，
        // 不需要消息队列重新发送消息，删掉redis中对于消息的记录(发送端的消息可靠机制)
        stringRedisTemplate.delete(SEND_ORDER_PAY_SUCCESS_INFO);

        //2.由于消费端手动开启确认机制，为解决消息重复消费的问题，消息消费后会在redis中存储id，在此处取出对应的value做检验。
        String messageFlag = stringRedisTemplate.opsForValue().get(CONSUME_ORDER_PAY_SUCCESS_INFO);

        //不为空说明已经消费过消息
        if (StringUtils.isNoneBlank(messageFlag)){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            return;
        }

        //根据传来的订单id修改订单状态，
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("state",ORDER_PAY_SUCCESS_STATE);
        updateWrapper.eq("orderSn",tradeNo);
        boolean update = orderService.update(updateWrapper);

        //给用户分配接口调用次数。
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("orderSn", tradeNo));
        if (order == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单不存在");
        }
        Long userId = order.getUserId();
        Long interfaceId = order.getInterfaceId();
        Integer count = order.getCount();
        boolean updateInvokeCount = apiBcakendService.updateUserInterfaceInvokeCount(userId, interfaceId, count);

        if (!update || !updateInvokeCount){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            return;
        }

        //消费成功，在redis中记录已经消费的消息（消费端的消费可靠机制）
        stringRedisTemplate.opsForValue().set(CONSUME_ORDER_PAY_SUCCESS_INFO + tradeNo, tradeNo,30, TimeUnit.MINUTES);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
