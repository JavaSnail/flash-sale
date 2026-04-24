package com.flashsale.seckill.execution.application;

import com.flashsale.seckill.execution.domain.event.SeckillOrderEvent;

public interface SeckillMessageSender {
    void sendSeckillOrder(SeckillOrderEvent event);
}
