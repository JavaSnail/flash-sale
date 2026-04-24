package com.flashsale.user.domain;

import java.time.LocalDateTime;

/**
 * 用户聚合根。
 * <p>
 * 用户是系统中的核心实体，承担身份认证与昵称管理的业务职责。 所有状态变更均通过业务方法完成，不暴露 setter，保护领域不变量。
 * </p>
 * <h3>生命周期</h3>
 * <ul>
 * <li><b>注册：</b>通过 {@link #register} 工厂方法创建，内部完成密码加密</li>
 * <li><b>登录：</b>调用 {@link #authenticate} 校验密码，失败抛出异常</li>
 * <li><b>修改昵称：</b>调用 {@link #changeNickname}，自动更新修改时间</li>
 * <li><b>持久化重建：</b>通过 {@link #reconstitute} 由 Repository 实现层使用</li>
 * </ul>
 * <h3>不变量</h3>
 * <ul>
 * <li>手机号不可变（注册后不可修改）</li>
 * <li>密码只能通过 {@link EncryptedPassword} 值对象管理</li>
 * <li>跨聚合不变量"手机号唯一"由 Application Service 保证（需查 Repository）</li>
 * </ul>
 *
 * @see PhoneNumber
 * @see EncryptedPassword
 * @see Nickname
 */
public class User {

    // ==================== 字段 ====================

    private final Long id;

    private final PhoneNumber phone;

    private Nickname nickname;

    private final EncryptedPassword password;

    private final LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ==================== 私有构造器 ====================

    private User(Long id, PhoneNumber phone, Nickname nickname, EncryptedPassword password, LocalDateTime createTime,
        LocalDateTime updateTime) {
        this.id = id;
        this.phone = phone;
        this.nickname = nickname;
        this.password = password;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ==================== 工厂方法 ====================

    /**
     * 注册新用户（领域工厂方法）。
     * <p>
     * 内部自动完成密码加盐加密，调用方无需关心加密细节。 返回的 User 对象 id 为 null，需通过 Repository 持久化后获取。
     * </p>
     *
     * @param phone 手机号值对象（已校验格式）
     * @param rawPassword 原文密码
     * @param nickname 昵称值对象（已校验长度）
     * @param encoder 密码编码器
     * @return 尚未持久化的用户聚合根（id = null）
     */
    public static User register(PhoneNumber phone, String rawPassword, Nickname nickname, PasswordEncoder encoder) {
        EncryptedPassword pwd = EncryptedPassword.encrypt(rawPassword, encoder);
        LocalDateTime now = LocalDateTime.now();
        return new User(null, phone, nickname, pwd, now, now);
    }

    /**
     * 从持久化存储重建领域对象。
     * <p>
     * <b>仅限 infrastructure 层（Repository 实现）使用。</b> 绕过业务校验直接构造，因为数据库中的数据已经是合法状态。
     * </p>
     *
     * @param id 数据库主键
     * @param phone 手机号值对象
     * @param nickname 昵称值对象
     * @param password 加密密码值对象
     * @param createTime 创建时间
     * @param updateTime 最后更新时间
     * @return 重建的用户聚合根
     */
    public static User reconstitute(Long id, PhoneNumber phone, Nickname nickname, EncryptedPassword password,
        LocalDateTime createTime, LocalDateTime updateTime) {
        return new User(id, phone, nickname, password, createTime, updateTime);
    }

    // ==================== 业务行为 ====================

    /**
     * 验证用户密码。
     * <p>
     * 与旧版 {@code checkPassword()} 返回 boolean 不同， 本方法遵循 "Tell, Don't Ask" 原则 —— 密码错误直接抛异常， 调用方无需做 if 判断。
     * </p>
     *
     * @param rawPassword 用户输入的原文密码
     * @param encoder 密码编码器
     * @throws InvalidCredentialsException 密码不匹配时抛出
     */
    public void authenticate(String rawPassword, PasswordEncoder encoder) {
        if (!password.matches(rawPassword, encoder)) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * 修改昵称。
     * <p>
     * 自动更新 {@code updateTime}，保证审计字段的一致性。
     * </p>
     *
     * @param newNickname 新昵称值对象（已校验长度）
     * @throws IllegalArgumentException 当新昵称为 null 时抛出
     */
    public void changeNickname(Nickname newNickname) {
        if (newNickname == null) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        this.nickname = newNickname;
        this.updateTime = LocalDateTime.now();
    }

    // ==================== 持久化辅助 ====================

    /**
     * 创建一个携带数据库生成 ID 的新 User 副本。
     * <p>
     * <b>仅限 Repository 实现在 insert 后使用。</b> 因为 User 的 id 字段为 final，无法通过 setter 赋值， 因此通过创建新实例的方式注入 ID。
     * </p>
     *
     * @param newId 数据库自增生成的 ID
     * @return 携带 ID 的新 User 实例（其余字段不变）
     */
    public User withId(Long newId) {
        return new User(newId, phone, nickname, password, createTime, updateTime);
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public EncryptedPassword getPassword() {
        return password;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
