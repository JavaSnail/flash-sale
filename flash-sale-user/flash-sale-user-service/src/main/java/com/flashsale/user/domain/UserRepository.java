package com.flashsale.user.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByPhone(String phone);
    void save(User user);
}
