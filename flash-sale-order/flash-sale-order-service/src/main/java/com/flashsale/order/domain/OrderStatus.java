package com.flashsale.order.domain;

/**
 * 订单状态枚举。
 * <p>
 * 状态机转换规则：
 * </p>
 * 
 * <pre>
 *   PENDING_PAYMENT ──→ PAID
 *        │
 *        └──────────→ CANCELLED
 * </pre>
 * <p>
 * 已支付的订单不能取消，已取消的订单不能支付。 状态转换逻辑内聚在 {@link Order} 聚合根的 {@code pay()} 和 {@code cancel()} 方法中。
 * </p>
 */
public enum OrderStatus {

    /** 待支付（下单后的初始状态） */
    PENDING_PAYMENT(0),

    /** 已支付 */
    PAID(1),

    /** 已取消 */
    CANCELLED(2);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    /**
     * 获取数据库持久化用的整数编码。
     */
    public int code() {
        return code;
    }

    /**
     * 从整数编码反查枚举值。
     *
     * @param code 数据库中存储的状态码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 未知状态码
     */
    public static OrderStatus fromCode(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知订单状态码: " + code);
    }
}
