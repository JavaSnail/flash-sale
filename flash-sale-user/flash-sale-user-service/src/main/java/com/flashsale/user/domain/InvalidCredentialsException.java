package com.flashsale.user.domain;

/**
 * 领域异常：认证失败。
 *
 * <p>当用户登录时手机号不存在或密码不匹配时，由 {@link User#authenticate} 抛出。
 * 统一以"手机号或密码错误"描述，避免泄露具体失败原因（安全最佳实践）。</p>
 *
 * @see User#authenticate(String, PasswordEncoder)
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("手机号或密码错误");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
