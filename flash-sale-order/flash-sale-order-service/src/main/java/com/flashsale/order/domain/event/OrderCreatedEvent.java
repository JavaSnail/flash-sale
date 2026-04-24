package com.flashsale.order.domain.event;

import java.io.Serializable;

public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private Long userId;
    private Long seckillGoodsId;

    public OrderCreatedEvent() {}
    public OrderCreatedEvent(Long orderId, Long userId, Long seckillGoodsId) {
        this.orderId = orderId;
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSeckillGoodsId() { return seckillGoodsId; }
    public void setSeckillGoodsId(Long seckillGoodsId) { this.seckillGoodsId = seckillGoodsId; }
}
