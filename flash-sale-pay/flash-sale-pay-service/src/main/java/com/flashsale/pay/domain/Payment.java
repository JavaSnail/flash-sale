package com.flashsale.pay.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付聚合根。
 * <p>
 * 管理支付的全生命周期：创建 → 成功/失败。 状态机转换包含守卫校验，只有 PENDING 状态才能转换。
 * </p>
 * <h3>状态机</h3>
 * 
 * <pre>
 *   PENDING ──→ SUCCESS（需提供 {@link TradeNo}）
 *      │
 *      └────→ FAIL
 * </pre>
 *
 * @see PaymentStatus
 * @see PayChannel
 * @see TradeNo
 */
public class Payment {

    // ==================== 字段 ====================

    private final Long id;

    private final Long orderId;

    private final Long userId;

    private final BigDecimal amount;

    private final PayChannel payChannel;

    private PaymentStatus status;

    private TradeNo tradeNo;

    private final LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ==================== 私有构造器 ====================

    private Payment(Long id, Long orderId, Long userId, BigDecimal amount, PayChannel payChannel, PaymentStatus status,
        TradeNo tradeNo, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.payChannel = payChannel;
        this.status = status;
        this.tradeNo = tradeNo;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 创建新支付记录。
     * <p>
     * 初始状态为 {@link PaymentStatus#PENDING}，{@link TradeNo} 为 null。
     * </p>
     *
     * @param orderId 关联订单 ID
     * @param userId 用户 ID
     * @param amount 支付金额（必须为正）
     * @param payChannel 支付渠道
     * @return 尚未持久化的支付记录（id = null）
     * @throws IllegalArgumentException 参数不完整或金额非法
     */
    public static Payment create(Long orderId, Long userId, BigDecimal amount, PayChannel payChannel) {
        if (orderId == null || userId == null) {
            throw new IllegalArgumentException("orderId/userId 不能为空");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("金额必须为正");
        }
        if (payChannel == null) {
            throw new IllegalArgumentException("支付渠道不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Payment(null, orderId, userId, amount, payChannel, PaymentStatus.PENDING, null, now, now);
    }

    /**
     * 从持久化存储重建领域对象。<b>仅限 infrastructure 层使用。</b>
     */
    public static Payment reconstitute(Long id, Long orderId, Long userId, BigDecimal amount, PayChannel payChannel,
        PaymentStatus status, TradeNo tradeNo, LocalDateTime createTime, LocalDateTime updateTime) {
        return new Payment(id, orderId, userId, amount, payChannel, status, tradeNo, createTime, updateTime);
    }

    // ==================== 状态机行为 ====================

    /**
     * 标记支付成功（PENDING → SUCCESS）。
     * <p>
     * 需要提供第三方支付渠道返回的交易号。
     * </p>
     *
     * @param tradeNo 第三方交易号
     * @throws IllegalStateException 当前状态非 PENDING
     * @throws IllegalArgumentException tradeNo 为 null
     */
    public void markSuccess(TradeNo tradeNo) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("仅 PENDING 支付可标记成功, 当前: " + status);
        }
        if (tradeNo == null) {
            throw new IllegalArgumentException("tradeNo 不能为空");
        }
        this.status = PaymentStatus.SUCCESS;
        this.tradeNo = tradeNo;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 标记支付失败（PENDING → FAIL）。
     *
     * @throws IllegalStateException 当前状态非 PENDING
     */
    public void markFail() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("仅 PENDING 支付可标记失败, 当前: " + status);
        }
        this.status = PaymentStatus.FAIL;
        this.updateTime = LocalDateTime.now();
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建携带数据库生成 ID 的新副本。<b>仅限 Repository 使用。</b>
     */
    public Payment withId(Long newId) {
        return new Payment(newId, orderId, userId, amount, payChannel, status, tradeNo, createTime, updateTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PayChannel getPayChannel() {
        return payChannel;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public TradeNo getTradeNo() {
        return tradeNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
