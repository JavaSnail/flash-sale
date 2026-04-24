package com.flashsale.seckill.execution.domain;

public class SeckillExecution {
    private final Long userId;
    private final Long seckillGoodsId;

    public SeckillExecution(Long userId, Long seckillGoodsId) {
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
    }

    public Long getUserId() { return userId; }
    public Long getSeckillGoodsId() { return seckillGoodsId; }
}
