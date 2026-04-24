package com.flashsale.order.adapter.mq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.order.application.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单超时取消 RocketMQ 消费者。
 * <p>
 * 监听 {@code order-timeout-topic} 的延时消息。 当订单在支付窗口期内未完成支付时，RocketMQ 延时消息到期触发本消费者， 自动取消订单并释放库存。
 * </p>
 * <p>
 * 消息体为纯文本的订单 ID。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MQConstants.ORDER_TIMEOUT_TOPIC, consumerGroup = "order-timeout-consumer-group")
public class OrderTimeoutConsumer implements RocketMQListener<String> {

    private final OrderService orderService;

    @Override
    public void onMessage(String message) {
        Long orderId = Long.parseLong(message);
        log.info("Order timeout, cancelling: orderId={}", orderId);
        orderService.cancelOrder(orderId);
    }
}
