package com.flashsale.seckill.execution.domain.event;

import java.io.Serializable;

public class SeckillOrderEvent implements Serializable {

    private Long userId;

    private Long seckillGoodsId;

    public SeckillOrderEvent() {
    }

    public SeckillOrderEvent(Long userId, Long seckillGoodsId) {
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSeckillGoodsId() {
        return seckillGoodsId;
    }

    public void setSeckillGoodsId(Long seckillGoodsId) {
        this.seckillGoodsId = seckillGoodsId;
    }
}
