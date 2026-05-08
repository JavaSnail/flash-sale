package com.flashsale.admin.config;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flashsale.common.result.ErrorCode;
import com.flashsale.common.result.Result;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(-1)
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e) {
        log.warn("未登录: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        log.warn("无权限: {}", e.getPermission());
        return Result.error(ErrorCode.FORBIDDEN);
    }

    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRole(NotRoleException e) {
        log.warn("无角色: {}", e.getRole());
        return Result.error(ErrorCode.FORBIDDEN);
    }
}
