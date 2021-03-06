package com.rabbit.study.ttl;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列
 * @author huangquan
 * @Date 2022/4/25
 *
 * 一般消息变成死信消息有如下几种情况：
 *  消息被拒绝(Basic.Reject/Basic.Nack) ，井且设置requeue 参数为false
 *  消息过期
 *  队列达到最大长度
 **/
@Configuration
public class DlxQueueConfig {
    public static final String JAVABOY_QUEUE_NAME = "javaboy_queue_name";
    public static final String JAVABOY_EXCHANGE_NAME = "javaboy_exchange_name";
    public static final String JAVABOY_ROUTING_KEY = "javaboy_routing_key";
    public static final String DLX_QUEUE_NAME = "dlx_queue_name";
    public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
    public static final String DLX_ROUTING_KEY = "dlx_routing_key";

    /**
     * 死信队列
     * @return
     */
    @Bean
    Queue dlxQueue() {
        return new Queue(DLX_QUEUE_NAME, true, false, false);
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean
    DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定死信队列和死信交换机
     * @return
     */
    @Bean
    Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }

    /**
     * 普通消息队列
     * @return
     */
    @Bean
    Queue javaboyQueue() {
        Map<String, Object> args = new HashMap<>();
        //配置消息队列时，为消息队列指定死信队列
        //设置消息过期时间
        args.put("x-message-ttl", 1000*10);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        //设置死信 routing_key
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        return new Queue(JAVABOY_QUEUE_NAME, true, false, false, args);
    }

    /**
     * 普通交换机
     * @return
     */
    @Bean
    DirectExchange javaboyExchange() {
        return new DirectExchange(JAVABOY_EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定普通队列和与之对应的交换机
     * @return
     */
    @Bean
    Binding javaboyBinding() {
        return BindingBuilder.bind(javaboyQueue())
                .to(javaboyExchange())
                .with(JAVABOY_ROUTING_KEY);
    }
}
