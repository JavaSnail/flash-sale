package com.flashsale.goods.domain;

import java.math.BigDecimal;

/**
 * 值对象：金额。
 *
 * <p>不可变对象，创建时校验非负约束。
 * 封装价格/金额概念，消除 {@link BigDecimal} 裸用导致的隐式规则散落。</p>
 *
 * @param amount 金额数值，必须 >= 0
 * @throws IllegalArgumentException 当 amount 为 null 或负数时抛出
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
     * 从 BigDecimal 构建金额值对象（语义化工厂方法）。
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    /**
     * 零金额。
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
}
