package com.flashsale.user.domain;

/**
 * 领域接口：密码编码器。
 * <p>
 * 定义密码加密、比对和盐值生成的契约。 具体实现（如 MD5、BCrypt）由 infrastructure 层提供， 遵循依赖倒置原则（DIP），domain 层不依赖具体加密算法。
 * </p>
 *
 * @see EncryptedPassword
 */
public interface PasswordEncoder {

    /**
     * 将原文密码与盐值组合后进行哈希编码。
     *
     * @param rawPassword 原文密码
     * @param salt 盐值
     * @return 哈希后的密码字符串
     */
    String encode(String rawPassword, String salt);

    /**
     * 验证原文密码是否与已编码密码匹配。
     *
     * @param rawPassword 用户输入的原文密码
     * @param encodedPassword 存储的哈希密码
     * @param salt 存储的盐值
     * @return 匹配返回 {@code true}
     */
    boolean matches(String rawPassword, String encodedPassword, String salt);

    /**
     * 生成随机盐值。
     *
     * @return 随机盐值字符串
     */
    String generateSalt();
}
