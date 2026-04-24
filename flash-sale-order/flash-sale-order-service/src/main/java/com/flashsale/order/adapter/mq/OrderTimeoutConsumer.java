package com.flashsale.order.adapter.mq;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.ORDER_TIMEOUT_TOPIC,
        consumerGroup = "order-timeout-consumer-group"
)
public class OrderTimeoutConsumer implements RocketMQListener<String> {

    private final OrderService orderService;

    @Override
    public void onMessage(String message) {
        Long orderId = Long.parseLong(message);
        log.info("Order timeout, cancelling: orderId={}", orderId);
        orderService.cancelOrder(orderId);
    }
}
