package com.flashsale.user.domain;

public interface PasswordEncoder {
    String encode(String rawPassword, String salt);
    boolean matches(String rawPassword, String encodedPassword, String salt);
    String generateSalt();
}
