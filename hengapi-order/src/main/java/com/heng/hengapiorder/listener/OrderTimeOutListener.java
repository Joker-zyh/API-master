package com.heng.hengapiorder.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heng.hengapicommon.model.entity.Order;
import com.heng.hengapicommon.service.ApiBcakendService;
import com.heng.hengapiorder.service.OrderService;
import com.rabbitmq.client.Channel;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;

import static com.heng.hengapiorder.config.RabbitMqConfig.DLX_QUEUE_ORDER_PAY;

@Component
@Slf4j
public class OrderTimeOutListener {
    /**
     * 1. 监听死信队列，若接收到消息说明此时订单过期
     * 2. 判断此时订单的支付状态，若支付则不需要操作；
     * 3. 若未支付，则回滚数据库，即修改数据库系统接口调用次数为原来的值，并修改订单状态为已过期。
     */
    @Resource
    private OrderService orderService;

    @Resource
    private ApiBcakendService apiBcakendService;

    private static final Integer ORDER_PAY_TIMEOUT_STATUS = 2;
    private static final Integer ORDER_UNPAY_STATUS = 0;

    @RabbitListener(queuesToDeclare = {@Queue(DLX_QUEUE_ORDER_PAY)})
    public void timeOut(Order order, Message message, Channel channel) throws IOException {
        log.info("监听到消息：" + order.toString());
        //根据order中的id，查询当前数据库中该order，判断现在order的支付状态
        Order orderNow = orderService.getById(order.getId());

        //订单超时，回滚数据库
        if (ORDER_UNPAY_STATUS.equals(orderNow.getState())){
            //修改数据库系统接口调用次数为原来的值
            Long interfaceId = orderNow.getInterfaceId();
            Integer count = orderNow.getCount();

            try {
                boolean recoverStock = apiBcakendService.recoverInterfaceStock(interfaceId, count);
                if (!recoverStock){
                    log.error("数据库回滚失败");
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }

                //修改订单状态为超时。
                UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("state",ORDER_PAY_TIMEOUT_STATUS)
                        .eq("id",orderNow.getId());
                orderService.update(updateWrapper);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            } catch (IOException e) {
                log.error("数据库回滚失败");
                e.printStackTrace();
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }

        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
