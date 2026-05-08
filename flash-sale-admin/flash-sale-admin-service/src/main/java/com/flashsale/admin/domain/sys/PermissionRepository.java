package com.flashsale.admin.domain.sys;

import java.util.List;

public interface PermissionRepository {
    List<Permission> findAll();
    List<Permission> findByRoleIds(List<Long> roleIds);
    List<String> findPermCodesByUserId(Long userId);
}
