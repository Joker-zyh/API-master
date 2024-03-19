package com.heng.hengapithirdparty.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.heng.hengapicommon.constant.RabbitmqConstant.*;

@Configuration
public class AliPayRabbitMqConfig {

    /**
     * 声明交换机
     * @return
     */
    @Bean
    public Exchange aliPayExchange(){
        return new DirectExchange(ORDER_EXCHANGE_NAME, true, false);
    }

    /**
     * 声明队列
     * @return
     */
    @Bean
    public Queue aliPayQueue(){
        /**
         * name: 队列名称
         * durable 是否持久化
         * exclusive 是否是排他队列 只有创建者可以使用
         * autoDelete 声明此队列为临时队列，最后一个消费者使用完自动删除
         */
        return new Queue(ORDER_SUCCESS_QUEUE_NAME, true,false,false,null);
    }

    /**
     * 交换机和队列绑定
     * @return
     */
    @Bean
    public Binding aliPayBinding(){
        return new Binding(ORDER_SUCCESS_QUEUE_NAME,Binding.DestinationType.QUEUE,
                ORDER_EXCHANGE_NAME,ORDER_SUCCESS_EXCHANGE_ROUTING_KEY,null);
    }

}
