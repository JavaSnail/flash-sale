package com.flashsale.order.adapter.mq;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.order.application.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.SECKILL_TOPIC,
        selectorExpression = MQConstants.SECKILL_ORDER_TAG,
        consumerGroup = "order-consumer-group"
)
public class SeckillOrderConsumer implements RocketMQListener<String> {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(String message) {
        try {
            SeckillOrderMessage event = objectMapper.readValue(message, SeckillOrderMessage.class);
            log.info("Consuming seckill order: userId={}, goodsId={}",
                    event.getUserId(), event.getSeckillGoodsId());
            orderService.createSeckillOrder(event.getUserId(), event.getSeckillGoodsId());
        } catch (Exception e) {
            log.error("Failed to process seckill order message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
