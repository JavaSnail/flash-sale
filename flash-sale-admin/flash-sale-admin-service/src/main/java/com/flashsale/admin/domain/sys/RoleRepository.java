package com.flashsale.admin.domain.sys;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    List<Role> findAll();
    Optional<Role> findById(Long id);
    Role save(Role role);
    void update(Role role);
    void deleteById(Long id);
    List<Role> findByUserId(Long userId);
    void assignRolesToUser(Long userId, List<Long> roleIds);
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
    void assignMenusToRole(Long roleId, List<Long> menuIds);
}
