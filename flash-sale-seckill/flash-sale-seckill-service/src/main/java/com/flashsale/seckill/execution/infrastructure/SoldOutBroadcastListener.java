package com.flashsale.seckill.execution.infrastructure;

import com.flashsale.seckill.execution.application.SeckillExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoldOutBroadcastListener implements MessageListener {

    private final SeckillExecutionService executionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String seckillGoodsId = new String(message.getBody());
        log.info("Received sold-out broadcast for seckillGoodsId={}", seckillGoodsId);
        executionService.onSoldOutBroadcast(Long.parseLong(seckillGoodsId));
    }
}
