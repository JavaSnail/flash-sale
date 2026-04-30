package com.flashsale.seckill.execution.infrastructure;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.flashsale.seckill.execution.application.SeckillExecutionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoldOutBroadcastListener implements MessageListener {

    private final SeckillExecutionService executionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        if (body.startsWith("reset:")) {
            Long id = Long.parseLong(body.substring(6));
            log.info("Received sold-out reset for seckillGoodsId={}", id);
            executionService.onSoldOutReset(id);
        } else {
            log.info("Received sold-out broadcast for seckillGoodsId={}", body);
            executionService.onSoldOutBroadcast(Long.parseLong(body));
        }
    }
}
