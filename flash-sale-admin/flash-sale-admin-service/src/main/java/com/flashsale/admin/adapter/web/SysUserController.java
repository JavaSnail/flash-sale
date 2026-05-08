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

import com.flashsale.admin.application.AdminUserVO;
import com.flashsale.admin.application.CreateUserRequest;
import com.flashsale.admin.application.SysUserService;
import com.flashsale.admin.application.UpdateUserRequest;
import com.flashsale.common.result.Result;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "管理员管理", description = "管理员用户CRUD + 分配角色")
@RestController
@RequestMapping("/admin/sys/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "查询管理员列表")
    @SaCheckPermission("admin:user:list")
    @GetMapping
    public Result<List<AdminUserVO>> list() {
        return Result.success(sysUserService.listUsers());
    }

    @Operation(summary = "查询管理员详情")
    @SaCheckPermission("admin:user:list")
    @GetMapping("/{id}")
    public Result<AdminUserVO> get(@PathVariable Long id) {
        return Result.success(sysUserService.getUser(id));
    }

    @Operation(summary = "创建管理员")
    @SaCheckPermission("admin:user:create")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateUserRequest request) {
        sysUserService.createUser(request);
        return Result.success();
    }

    @Operation(summary = "编辑管理员")
    @SaCheckPermission("admin:user:update")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        sysUserService.updateUser(id, request);
        return Result.success();
    }

    @Operation(summary = "删除管理员")
    @SaCheckPermission("admin:user:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "分配角色")
    @SaCheckPermission("admin:user:assign-role")
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(id, roleIds);
        return Result.success();
    }
}
