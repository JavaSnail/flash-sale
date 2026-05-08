package com.flashsale.admin.config;

import java.util.List;

import org.springframework.stereotype.Component;

import com.flashsale.admin.domain.sys.PermissionRepository;
import com.flashsale.admin.domain.sys.Role;
import com.flashsale.admin.domain.sys.RoleRepository;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;

/**
 * Sa-Token 权限/角色数据提供者。
 * Sa-Token 在鉴权时回调此接口获取当前用户的角色列表和权限列表。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return permissionRepository.findPermCodesByUserId(userId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return roleRepository.findByUserId(userId).stream()
                .map(Role::getRoleCode).toList();
    }
}
