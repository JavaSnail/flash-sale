package com.flashsale.pay.domain;

/**
 * 支付渠道枚举。
 *
 * <p>枚举化支付渠道，替代原来的 String 类型，
 * 编译期发现非法渠道值，运行期提供清晰的错误信息。</p>
 */
public enum PayChannel {

    /** 支付宝 */
    ALIPAY,

    /** 微信支付 */
    WECHAT,

    /** 余额支付 */
    BALANCE;

    /**
     * 从字符串名称（忽略大小写）构建枚举。
     *
     * @param name 渠道名称（如 "alipay"、"WECHAT"）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 渠道名称为 null 或不存在
     */
    public static PayChannel of(String name) {
        if (name == null) {
            throw new IllegalArgumentException("支付渠道不能为空");
        }
        return PayChannel.valueOf(name.toUpperCase());
    }
}
