package com.flashsale.admin.adapter.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.admin.application.AdminAuthService;
import com.flashsale.admin.application.AdminLoginVO;
import com.flashsale.common.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Tag(name = "管理员认证", description = "管理员登录/登出/获取当前用户")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody LoginRequest request) {
        AdminLoginVO vo = adminAuthService.login(request.getUsername(), request.getPassword());
        return Result.success(vo);
    }

    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        adminAuthService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<AdminLoginVO> me() {
        return Result.success(adminAuthService.getCurrentUser());
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
