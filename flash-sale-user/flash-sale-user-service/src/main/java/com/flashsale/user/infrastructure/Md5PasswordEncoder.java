package com.flashsale.user.infrastructure;

import org.springframework.stereotype.Component;

import com.flashsale.user.domain.PasswordEncoder;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * 基于 MD5 的密码编码器实现。
 * <p>
 * 采用"双重 MD5 + 双盐"策略：{@code md5(md5(password + fixedSalt) + randomSalt)}。
 * </p>
 * <ul>
 * <li><b>固定盐（FIXED_SALT）</b>：编译期常量，防止简单彩虹表攻击</li>
 * <li><b>随机盐（randomSalt）</b>：每个用户独立生成并存储，防止相同密码产生相同哈希</li>
 * </ul>
 *
 * @see com.flashsale.user.domain.PasswordEncoder
 * @see com.flashsale.user.domain.EncryptedPassword
 */
@Component
public class Md5PasswordEncoder implements PasswordEncoder {

    /** 固定盐，与随机盐叠加使用，增加破解难度 */
    private static final String FIXED_SALT = "f1a5h$@le";

    /**
     * 加密原文密码：第一轮 MD5 混入固定盐，第二轮 MD5 混入随机盐。
     */
    @Override
    public String encode(String rawPassword, String salt) {
        String firstMd5 = SecureUtil.md5(rawPassword + FIXED_SALT);
        return SecureUtil.md5(firstMd5 + salt);
    }

    /**
     * 校验密码：用相同的盐重新加密原文，比较哈希值是否一致。
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword, String salt) {
        return encode(rawPassword, salt).equals(encodedPassword);
    }

    /**
     * 生成 8 位随机盐（字母 + 数字）。
     */
    @Override
    public String generateSalt() {
        return RandomUtil.randomString(8);
    }
}
