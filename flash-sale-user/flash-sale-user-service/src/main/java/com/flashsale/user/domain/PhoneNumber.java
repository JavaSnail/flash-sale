package com.flashsale.user.domain;

import java.util.regex.Pattern;

/**
 * 值对象：手机号。
 * <p>
 * 不可变对象，创建时自动校验中国大陆手机号格式（11 位，1 开头）。 作为 {@link User} 聚合根的身份标识之一，同时承担格式合法性的自校验职责。
 * </p>
 *
 * @param value 原始手机号字符串，必须匹配 {@code ^1[3-9]\d{9}$}
 * @throws IllegalArgumentException 当手机号为 null 或格式不合法时抛出
 */
public record PhoneNumber(String value) {

    /** 中国大陆手机号正则：1 开头，第二位 3~9，后跟 9 位数字 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    public PhoneNumber {
        if (value == null || !PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("手机号格式不合法: " + value);
        }
    }
}
