package com.flashsale.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long userId;
    private Long seckillGoodsId;
    private Long goodsId;
    private BigDecimal orderPrice;
    private Integer status; // 0-待支付 1-已支付 2-已取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static Order createSeckillOrder(Long userId, Long seckillGoodsId, Long goodsId, BigDecimal price) {
        Order order = new Order();
        order.userId = userId;
        order.seckillGoodsId = seckillGoodsId;
        order.goodsId = goodsId;
        order.orderPrice = price;
        order.status = 0;
        order.createTime = LocalDateTime.now();
        order.updateTime = LocalDateTime.now();
        return order;
    }

    public void pay() { this.status = 1; this.updateTime = LocalDateTime.now(); }
    public void cancel() { this.status = 2; this.updateTime = LocalDateTime.now(); }
    public boolean isPending() { return this.status == 0; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSeckillGoodsId() { return seckillGoodsId; }
    public void setSeckillGoodsId(Long seckillGoodsId) { this.seckillGoodsId = seckillGoodsId; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public BigDecimal getOrderPrice() { return orderPrice; }
    public void setOrderPrice(BigDecimal orderPrice) { this.orderPrice = orderPrice; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
