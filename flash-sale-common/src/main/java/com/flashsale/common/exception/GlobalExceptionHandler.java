package com.flashsale.common.exception;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.flashsale.common.result.ErrorCode;
import com.flashsale.common.result.Result;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器。
 *
 * <p>捕获所有 Controller 抛出的异常，统一转换为 {@link Result} 格式响应。
 * HTTP 状态码始终返回 200，业务错误通过 Result.code 区分。
 * 错误响应自动携带 traceId，便于客户端反馈问题时排查。</p>
 * <p>通过 {@link AutoConfiguration} + {@code AutoConfiguration.imports} 注册，
 * 所有依赖 flash-sale-common 的微服务自动生效。</p>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常 — 由各服务主动抛出
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, msg={}", e.getErrorCode().getCode(), e.getMessage());
        return Result.error(e.getErrorCode().getCode(), e.getMessage());
    }

    /**
     * @Valid 校验异常 — @RequestBody 对象校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
        log.warn("参数校验失败: {}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 表单参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
        log.warn("参数绑定失败: {}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * @Validated 校验异常 — Controller 类上的单参数校验（如 @RequestParam @NotBlank）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("参数错误");
        log.warn("约束校验失败: {}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * JSON 解析失败 — 请求体格式错误、字段类型不匹配等
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), "请求体格式错误");
    }

    /**
     * 请求方法不支持 — GET 接口被 POST 访问等
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), "请求方法不支持: " + e.getMethod());
    }

    /**
     * 404 — 需配合 spring.mvc.throw-exception-if-no-handler-found=true 才会触发
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("接口不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return Result.error(ErrorCode.NOT_FOUND);
    }

    /**
     * 领域层参数校验 — 值对象、实体构造时的 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数非法: {}", e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 兜底 — 未预期的异常，不向前端暴露具体信息（避免泄露 SQL/堆栈）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}