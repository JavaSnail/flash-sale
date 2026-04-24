package com.flashsale.pay.domain;

/**
 * 值对象：第三方支付交易号。
 * <p>
 * 封装支付渠道返回的交易流水号，不可变对象。 仅在支付成功时由 {@link Payment#markSuccess(TradeNo)} 写入。
 * </p>
 *
 * @param value 交易号字符串，不能为空
 */
public record TradeNo(String value) {

    public TradeNo {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tradeNo 不能为空");
        }
    }

    /**
     * 语义化工厂方法。
     */
    public static TradeNo of(String value) {
        return new TradeNo(value);
    }
}
