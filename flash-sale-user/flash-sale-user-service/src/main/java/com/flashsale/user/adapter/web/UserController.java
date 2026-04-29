package com.flashsale.user.adapter.web;

import com.flashsale.common.result.Result;
import com.flashsale.user.api.dto.LoginRequest;
import com.flashsale.user.api.dto.RegisterRequest;
import com.flashsale.user.api.dto.UserDTO;
import com.flashsale.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户注册、登录、查询接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "通过手机号和密码登录，返回token")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @Operation(summary = "根据ID查询用户", description = "根据用户ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserDTO> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "获取当前用户信息", description = "根据登录态获取当前用户信息")
    @GetMapping("/me")
    public Result<UserDTO> me() {
        return Result.success(userService.getCurrentUser());
    }
}
