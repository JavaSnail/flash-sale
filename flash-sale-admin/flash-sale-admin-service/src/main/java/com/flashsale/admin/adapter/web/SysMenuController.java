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

import com.flashsale.admin.application.CreateMenuRequest;
import com.flashsale.admin.application.MenuVO;
import com.flashsale.admin.application.SysMenuService;
import com.flashsale.admin.application.UpdateMenuRequest;
import com.flashsale.common.result.Result;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "菜单管理", description = "菜单CRUD + 树形查询")
@RestController
@RequestMapping("/admin/sys/menus")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "查询菜单树")
    @SaCheckPermission("admin:menu:list")
    @GetMapping
    public Result<List<MenuVO>> tree() {
        return Result.success(sysMenuService.listMenuTree());
    }

    @Operation(summary = "查询菜单列表(平铺)")
    @SaCheckPermission("admin:menu:list")
    @GetMapping("/flat")
    public Result<List<MenuVO>> flat() {
        return Result.success(sysMenuService.listMenuFlat());
    }

    @Operation(summary = "创建菜单")
    @SaCheckPermission("admin:menu:create")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateMenuRequest request) {
        sysMenuService.createMenu(request);
        return Result.success();
    }

    @Operation(summary = "编辑菜单")
    @SaCheckPermission("admin:menu:update")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateMenuRequest request) {
        sysMenuService.updateMenu(id, request);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @SaCheckPermission("admin:menu:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }
}
