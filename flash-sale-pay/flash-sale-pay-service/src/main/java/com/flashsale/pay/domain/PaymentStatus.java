package com.flashsale.pay.domain;

/**
 * 支付状态枚举。
 * <p>
 * 状态机转换规则：
 * </p>
 * 
 * <pre>
 *   PENDING ──→ SUCCESS
 *      │
 *      └────→ FAIL
 * </pre>
 * <p>
 * 终态（SUCCESS / FAIL）不可再转换。 状态转换逻辑内聚在 {@link Payment#markSuccess} 和 {@link Payment#markFail} 中。
 * </p>
 */
public enum PaymentStatus {

    /** 待支付（初始状态） */
    PENDING(0),

    /** 支付成功 */
    SUCCESS(1),

    /** 支付失败 */
    FAIL(2);

    private final int code;

    PaymentStatus(int code) {
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
    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知支付状态码: " + code);
    }
}
