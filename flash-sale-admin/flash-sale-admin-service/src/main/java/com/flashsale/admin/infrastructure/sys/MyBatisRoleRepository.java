package com.flashsale.admin.infrastructure.sys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.admin.domain.sys.Role;
import com.flashsale.admin.domain.sys.RoleRepository;
import com.flashsale.admin.infrastructure.sys.mapper.AdminUserRoleMapper;
import com.flashsale.admin.infrastructure.sys.mapper.RoleMapper;
import com.flashsale.admin.infrastructure.sys.mapper.RoleMenuMapper;
import com.flashsale.admin.infrastructure.sys.mapper.RolePermissionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisRoleRepository implements RoleRepository {

    private final RoleMapper roleMapper;
    private final AdminUserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                .orderByAsc(RoleDO::getSortOrder))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Role> findById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return Optional.ofNullable(roleDO).map(this::toDomain);
    }

    @Override
    public Role save(Role role) {
        RoleDO roleDO = toDO(role);
        roleMapper.insert(roleDO);
        return toDomain(roleDO);
    }

    @Override
    public void update(Role role) {
        RoleDO roleDO = toDO(role);
        roleDO.setId(role.getId());
        roleMapper.updateById(roleDO);
    }

    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
        // Clean up relations
        userRoleMapper.delete(new LambdaQueryWrapper<AdminUserRoleDO>().eq(AdminUserRoleDO::getRoleId, id));
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, id));
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, id));
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<AdminUserRoleDO>().eq(AdminUserRoleDO::getUserId, userId))
                .stream().map(AdminUserRoleDO::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return roleMapper.selectBatchIds(roleIds).stream().map(this::toDomain).toList();
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // Remove existing
        userRoleMapper.delete(new LambdaQueryWrapper<AdminUserRoleDO>().eq(AdminUserRoleDO::getUserId, userId));
        // Assign new
        for (Long roleId : roleIds) {
            AdminUserRoleDO rel = new AdminUserRoleDO();
            rel.setUserId(userId);
            rel.setRoleId(roleId);
            rel.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(rel);
        }
    }

    @Override
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId));
        for (Long permId : permissionIds) {
            RolePermissionDO rel = new RolePermissionDO();
            rel.setRoleId(roleId);
            rel.setPermissionId(permId);
            rel.setCreateTime(LocalDateTime.now());
            rolePermissionMapper.insert(rel);
        }
    }

    @Override
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, roleId));
        for (Long menuId : menuIds) {
            RoleMenuDO rel = new RoleMenuDO();
            rel.setRoleId(roleId);
            rel.setMenuId(menuId);
            rel.setCreateTime(LocalDateTime.now());
            roleMenuMapper.insert(rel);
        }
    }

    private Role toDomain(RoleDO roleDO) {
        return Role.builder()
                .id(roleDO.getId())
                .roleCode(roleDO.getRoleCode())
                .roleName(roleDO.getRoleName())
                .description(roleDO.getDescription())
                .status(roleDO.getStatus())
                .sortOrder(roleDO.getSortOrder())
                .createTime(roleDO.getCreateTime())
                .updateTime(roleDO.getUpdateTime())
                .build();
    }

    private RoleDO toDO(Role role) {
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleCode(role.getRoleCode());
        roleDO.setRoleName(role.getRoleName());
        roleDO.setDescription(role.getDescription());
        roleDO.setStatus(role.getStatus());
        roleDO.setSortOrder(role.getSortOrder());
        return roleDO;
    }
}
