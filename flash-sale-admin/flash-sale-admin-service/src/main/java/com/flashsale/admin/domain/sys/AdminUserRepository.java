package com.flashsale.admin.domain.sys;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findById(Long id);
    List<AdminUser> findAll();
    AdminUser save(AdminUser user);
    void update(AdminUser user);
    void updateLastLoginTime(Long id);
    void deleteById(Long id);
}
