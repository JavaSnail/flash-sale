package com.flashsale.user.domain;

/**
 * 值对象：用户昵称。
 * <p>
 * 不可变对象，创建时校验长度范围（1~32 字符）。 空白字符串视为无效输入。
 * </p>
 *
 * @param value 昵称字符串，长度 1~32
 * @throws IllegalArgumentException 当昵称为空或超长时抛出
 */
public record Nickname(String value) {

    /** 昵称最大长度 */
    private static final int MAX_LENGTH = 32;

    public Nickname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("昵称长度不能超过 " + MAX_LENGTH);
        }
    }
}
