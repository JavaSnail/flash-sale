package com.flashsale.admin.domain;

import java.math.BigDecimal;

/**
 * 值对象：金额（非负）。
 * <p>
 * 与 goods 模块的 Money 值对象功能相同， 因 admin 模块不依赖 goods-service 而独立定义。
 * </p>
 *
 * @param amount 金额数值，必须 >= 0
 */
public record Money(BigDecimal amount) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("金额不能为负: " + amount);
        }
    }

    /**
     * 从 BigDecimal 构建金额值对象。
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
}
