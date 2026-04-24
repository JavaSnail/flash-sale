package com.flashsale.order.adapter.mq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.common.constant.MQConstants;
import com.flashsale.order.application.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 秒杀订单 RocketMQ 消费者。
 * <p>
 * 监听 {@code seckill-topic} 主题、{@code seckill-order} Tag 的消息， 将异步秒杀请求转化为实际的订单创建操作。
 * </p>
 * <p>
 * 消息由秒杀服务在库存预扣成功后发送，本消费者负责：
 * </p>
 * <ol>
 * <li>反序列化 {@link SeckillOrderMessage}（userId + seckillGoodsId）</li>
 * <li>委托 {@link OrderService#createSeckillOrder} 完成订单创建（含幂等检查）</li>
 * <li>消费失败时抛出 RuntimeException，RocketMQ 将自动重试</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MQConstants.SECKILL_TOPIC, selectorExpression = MQConstants.SECKILL_ORDER_TAG,
    consumerGroup = "order-consumer-group")
public class SeckillOrderConsumer implements RocketMQListener<String> {

    private final OrderService orderService;

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(String message) {
        try {
            SeckillOrderMessage event = objectMapper.readValue(message, SeckillOrderMessage.class);
            log.info("Consuming seckill order: userId={}, goodsId={}", event.getUserId(), event.getSeckillGoodsId());
            orderService.createSeckillOrder(event.getUserId(), event.getSeckillGoodsId());
        }
        catch (Exception e) {
            log.error("Failed to process seckill order message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
