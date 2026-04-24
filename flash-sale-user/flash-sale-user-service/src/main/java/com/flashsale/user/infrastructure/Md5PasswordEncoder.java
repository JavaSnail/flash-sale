package com.flashsale.user.infrastructure;

import com.flashsale.user.domain.PasswordEncoder;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Component;

@Component
public class Md5PasswordEncoder implements PasswordEncoder {

    private static final String FIXED_SALT = "f1a5h$@le";

    @Override
    public String encode(String rawPassword, String salt) {
        // Double MD5: md5(md5(password + fixedSalt) + salt)
        String firstMd5 = SecureUtil.md5(rawPassword + FIXED_SALT);
        return SecureUtil.md5(firstMd5 + salt);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword, String salt) {
        return encode(rawPassword, salt).equals(encodedPassword);
    }

    @Override
    public String generateSalt() {
        return RandomUtil.randomString(8);
    }
}
