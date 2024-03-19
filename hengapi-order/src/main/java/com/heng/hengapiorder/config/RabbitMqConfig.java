package com.heng.hengapiorder.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    public static final String QUEUE_ORDER_PAY = "queue_order_pay";
    public static final String EXCHANGE_ORDER_PAY = "exchange_order_pay";
    public static final String ROUTING_KEY_ORDER_PAY = "routing.order.pay";

    public static final String DLX_QUEUE_ORDER_PAY = "dlx_queue_order_pay";
    public static final String DLX_EXCHANGE_ORDER_PAY = "dlx_exchange_order_pay";
    public static final String DLX_ROUTING_KEY_ORDER_PAY = "routing.dlx.order.pay";

    //死信交换机
    @Bean
    public Exchange DLX_EXCHANGE_ORDER_PAY(){
        return new DirectExchange(DLX_EXCHANGE_ORDER_PAY,true,false);
    }

    //死信队列
    @Bean
    public Queue DLX_QUEUE_ORDER_PAY(){
        return new Queue(DLX_QUEUE_ORDER_PAY, true, false,false);
    }

    //绑定
    @Bean
    public Binding DLX_BINDING(){
        return new Binding(DLX_QUEUE_ORDER_PAY,Binding.DestinationType.QUEUE,
                DLX_EXCHANGE_ORDER_PAY,DLX_ROUTING_KEY_ORDER_PAY,null);
    }

    //定义订单支付队列
    @Bean
    public Queue QUEUE_ORDER_PAY(){
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", DLX_EXCHANGE_ORDER_PAY);
        map.put("x-dead-letter-routing-key",DLX_ROUTING_KEY_ORDER_PAY);
        map.put("x-message-ttl",1000*60*30);

        return new Queue(QUEUE_ORDER_PAY,true,false,false,map);
    }

    //订单支付交换机
    @Bean
    public Exchange EXCHANGE_ORDER_PAY(){
        return new DirectExchange(EXCHANGE_ORDER_PAY,true,false);
    }

    //绑定
    @Bean
    public Binding BINDING(){
        return new Binding(QUEUE_ORDER_PAY,Binding.DestinationType.QUEUE,
                EXCHANGE_ORDER_PAY,ROUTING_KEY_ORDER_PAY,null);
    }
}
