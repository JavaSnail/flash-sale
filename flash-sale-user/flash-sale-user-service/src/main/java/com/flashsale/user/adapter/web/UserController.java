package com.flashsale.user.adapter.web;

import com.flashsale.common.result.Result;
import com.flashsale.user.api.dto.LoginRequest;
import com.flashsale.user.api.dto.RegisterRequest;
import com.flashsale.user.api.dto.UserDTO;
import com.flashsale.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/me")
    public Result<UserDTO> me() {
        return Result.success(userService.getCurrentUser());
    }
}
