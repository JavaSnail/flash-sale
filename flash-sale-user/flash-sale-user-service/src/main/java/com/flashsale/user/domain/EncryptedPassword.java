package com.flashsale.user.domain;

/**
 * 值对象：加密后的密码。
 * <p>
 * 封装密码哈希值与盐值的不可变组合，承担密码加密与验证的核心逻辑。 将密码的"如何加密"和"如何比对"职责内聚在此对象中， 避免密码处理逻辑散落在 Service 层。
 * </p>
 * <p>
 * 使用方式：
 * </p>
 * <ul>
 * <li>新建：{@link #encrypt(String, PasswordEncoder)} — 注册时加密原文密码</li>
 * <li>重建：{@link #of(String, String)} — 持久化层从数据库加载已有密码</li>
 * <li>比对：{@link #matches(String, PasswordEncoder)} — 登录时校验密码</li>
 * </ul>
 *
 * @param hash 密码哈希值，由 {@link PasswordEncoder} 生成
 * @param salt 随机盐值，由 {@link PasswordEncoder} 生成
 */
public record EncryptedPassword(String hash, String salt) {

    public EncryptedPassword {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("password hash 不能为空");
        }
        if (salt == null || salt.isBlank()) {
            throw new IllegalArgumentException("salt 不能为空");
        }
    }

    // ==================== 工厂方法 ====================

    /**
     * 加密原文密码，生成新的 {@link EncryptedPassword}。
     * <p>
     * 内部自动生成盐值并调用 encoder 完成哈希计算。 用于用户注册场景。
     * </p>
     *
     * @param rawPassword 原文密码，不能为空
     * @param encoder 密码编码器（领域接口，实现由 infrastructure 层提供）
     * @return 包含 hash 和 salt 的不可变密码对象
     * @throws IllegalArgumentException 当原文密码为空时抛出
     */
    public static EncryptedPassword encrypt(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        String salt = encoder.generateSalt();
        String hash = encoder.encode(rawPassword, salt);
        return new EncryptedPassword(hash, salt);
    }

    /**
     * 从已有的哈希值和盐值重建密码对象。
     * <p>
     * <b>仅限持久化层使用</b>（从数据库加载时调用）。
     * </p>
     *
     * @param hash 数据库中存储的密码哈希
     * @param salt 数据库中存储的盐值
     * @return 重建的密码对象
     */
    public static EncryptedPassword of(String hash, String salt) {
        return new EncryptedPassword(hash, salt);
    }

    // ==================== 业务行为 ====================

    /**
     * 校验原文密码是否与当前加密密码匹配。
     *
     * @param rawPassword 用户输入的原文密码
     * @param encoder 密码编码器
     * @return 匹配返回 {@code true}，否则 {@code false}
     */
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, hash, salt);
    }
}
