package com.flashsale.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单聚合根。
 * <p>
 * 封装秒杀订单的全生命周期：创建 → 支付/取消。 状态机转换逻辑内聚在 {@link #pay()} 和 {@link #cancel()} 方法中， 包含非法转换的守卫校验。
 * </p>
 * <h3>状态机</h3>
 * 
 * <pre>
 *   PENDING_PAYMENT ──→ PAID
 *        │
 *        └──────────→ CANCELLED
 * </pre>
 *
 * @see OrderStatus
 */
public class Order {

    // ==================== 字段 ====================

    private final Long id;

    private final Long userId;

    private final Long seckillGoodsId;

    private final Long goodsId;

    private final BigDecimal orderPrice;

    private OrderStatus status;

    private final LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ==================== 私有构造器 ====================

    private Order(Long id, Long userId, Long seckillGoodsId, Long goodsId, BigDecimal orderPrice, OrderStatus status,
        LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
        this.goodsId = goodsId;
        this.orderPrice = orderPrice;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 创建秒杀订单。
     * <p>
     * 初始状态为 {@link OrderStatus#PENDING_PAYMENT}。 由 MQ 消费者在消费秒杀消息后调用。
     * </p>
     *
     * @param userId 用户 ID
     * @param seckillGoodsId 秒杀商品 ID
     * @param goodsId 原始商品 ID
     * @param price 订单价格（秒杀价）
     * @return 尚未持久化的订单（id = null）
     * @throws IllegalArgumentException 参数不完整或价格非法
     */
    public static Order createSeckillOrder(Long userId, Long seckillGoodsId, Long goodsId, BigDecimal price) {
        if (userId == null || seckillGoodsId == null || goodsId == null) {
            throw new IllegalArgumentException("订单字段不完整");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("订单价格不合法");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Order(null, userId, seckillGoodsId, goodsId, price, OrderStatus.PENDING_PAYMENT, now, now);
    }

    /**
     * 从持久化存储重建领域对象。<b>仅限 infrastructure 层使用。</b>
     */
    public static Order reconstitute(Long id, Long userId, Long seckillGoodsId, Long goodsId, BigDecimal orderPrice,
        OrderStatus status, LocalDateTime createTime, LocalDateTime updateTime) {
        return new Order(id, userId, seckillGoodsId, goodsId, orderPrice, status, createTime, updateTime);
    }

    // ==================== 状态机行为 ====================

    /**
     * 支付订单（PENDING_PAYMENT → PAID）。
     *
     * @throws IllegalStateException 已支付或已取消的订单不能再支付
     */
    public void pay() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("订单已支付");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("订单已取消，不能支付");
        }
        this.status = OrderStatus.PAID;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 取消订单（PENDING_PAYMENT → CANCELLED）。
     *
     * @throws IllegalStateException 已支付或已取消的订单不能取消
     */
    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("订单已支付，不能取消");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("订单已取消");
        }
        this.status = OrderStatus.CANCELLED;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 判断订单是否处于待支付状态。
     */
    public boolean isPending() {
        return status == OrderStatus.PENDING_PAYMENT;
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建携带数据库生成 ID 的新副本。<b>仅限 Repository 使用。</b>
     */
    public Order withId(Long newId) {
        return new Order(newId, userId, seckillGoodsId, goodsId, orderPrice, status, createTime, updateTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeckillGoodsId() {
        return seckillGoodsId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
