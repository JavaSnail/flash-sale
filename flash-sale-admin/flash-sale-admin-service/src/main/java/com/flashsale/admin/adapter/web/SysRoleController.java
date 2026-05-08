package com.flashsale.admin.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.admin.application.CreateRoleRequest;
import com.flashsale.admin.application.RoleVO;
import com.flashsale.admin.application.SysRoleService;
import com.flashsale.admin.application.UpdateRoleRequest;
import com.flashsale.common.result.Result;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "角色管理", description = "角色CRUD + 分配权限/菜单")
@RestController
@RequestMapping("/admin/sys/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "查询角色列表")
    @SaCheckPermission("admin:role:list")
    @GetMapping
    public Result<List<RoleVO>> list() {
        return Result.success(sysRoleService.listRoles());
    }

    @Operation(summary = "查询角色详情")
    @SaCheckPermission("admin:role:list")
    @GetMapping("/{id}")
    public Result<RoleVO> get(@PathVariable Long id) {
        return Result.success(sysRoleService.getRole(id));
    }

    @Operation(summary = "创建角色")
    @SaCheckPermission("admin:role:create")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateRoleRequest request) {
        sysRoleService.createRole(request);
        return Result.success();
    }

    @Operation(summary = "编辑角色")
    @SaCheckPermission("admin:role:update")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        sysRoleService.updateRole(id, request);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @SaCheckPermission("admin:role:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "分配权限")
    @SaCheckPermission("admin:role:assign-perm")
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        sysRoleService.assignPermissions(id, permissionIds);
        return Result.success();
    }

    @Operation(summary = "分配菜单")
    @SaCheckPermission("admin:role:assign-menu")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return Result.success();
    }
}
