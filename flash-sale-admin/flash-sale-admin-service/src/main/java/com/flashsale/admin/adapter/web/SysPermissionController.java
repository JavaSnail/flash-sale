package com.flashsale.admin.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.admin.domain.sys.Permission;
import com.flashsale.admin.domain.sys.PermissionRepository;
import com.flashsale.common.result.Result;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "权限管理", description = "权限列表查询")
@RestController
@RequestMapping("/admin/sys/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final PermissionRepository permissionRepository;

    @Operation(summary = "查询权限列表")
    @SaCheckPermission("admin:perm:list")
    @GetMapping
    public Result<List<Permission>> list() {
        return Result.success(permissionRepository.findAll());
    }
}
