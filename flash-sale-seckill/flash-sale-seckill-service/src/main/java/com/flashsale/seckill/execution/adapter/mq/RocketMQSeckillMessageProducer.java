package com.flashsale.seckill.execution.adapter.mq;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.seckill.execution.application.SeckillMessageSender;
import com.flashsale.seckill.execution.domain.event.SeckillOrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RocketMQSeckillMessageProducer implements SeckillMessageSender {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendSeckillOrder(SeckillOrderEvent event) {
        String json = objectMapper.writeValueAsString(event);
        String destination = MQConstants.SECKILL_TOPIC + ":" + MQConstants.SECKILL_ORDER_TAG;
        rocketMQTemplate.send(destination, MessageBuilder.withPayload(json).build());
    }
}
