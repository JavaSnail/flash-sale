package com.flashsale.pay.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String payChannel;
    private Integer status; // 0-待支付 1-成功 2-失败
    private String tradeNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static Payment create(Long orderId, Long userId, BigDecimal amount, String payChannel) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.userId = userId;
        p.amount = amount;
        p.payChannel = payChannel;
        p.status = 0;
        p.createTime = LocalDateTime.now();
        p.updateTime = LocalDateTime.now();
        return p;
    }

    public void markSuccess(String tradeNo) {
        this.status = 1;
        this.tradeNo = tradeNo;
        this.updateTime = LocalDateTime.now();
    }

    public void markFail() {
        this.status = 2;
        this.updateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayChannel() { return payChannel; }
    public void setPayChannel(String payChannel) { this.payChannel = payChannel; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getTradeNo() { return tradeNo; }
    public void setTradeNo(String tradeNo) { this.tradeNo = tradeNo; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
