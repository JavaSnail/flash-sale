package com.flashsale.admin.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flashsale.admin.domain.sys.Permission;
import com.flashsale.admin.domain.sys.PermissionRepository;
import com.flashsale.admin.domain.sys.Role;
import com.flashsale.admin.domain.sys.RoleRepository;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleVO> listRoles() {
        return roleRepository.findAll().stream().map(this::toVO).toList();
    }

    public RoleVO getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        RoleVO vo = toVO(role);
        // Load assigned permission IDs
        List<Permission> perms = permissionRepository.findByRoleIds(List.of(id));
        vo.setPermissionIds(perms.stream().map(Permission::getId).toList());
        return vo;
    }

    public void createRole(CreateRoleRequest request) {
        Role role = Role.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .status(1)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        roleRepository.save(role);
    }

    public void updateRole(Long id, UpdateRoleRequest request) {
        roleRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        Role role = Role.builder()
                .id(id)
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .status(request.getStatus())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        roleRepository.update(role);
    }

    public void deleteRole(Long id) {
        roleRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        roleRepository.deleteById(id);
    }

    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        roleRepository.assignPermissionsToRole(roleId, permissionIds);
    }

    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        roleRepository.assignMenusToRole(roleId, menuIds);
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setSortOrder(role.getSortOrder());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
