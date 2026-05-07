package com.flashsale.common.result;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应包装器。
 * <p>
 * 所有接口返回值均使用此对象封装，包含：
 * </p>
 * <ul>
 * <li>{@code code} — 0 表示成功，非 0 为错误码</li>
 * <li>{@code msg} — 提示信息</li>
 * <li>{@code data} — 业务数据（泛型）</li>
 * </ul>
 */
@Schema(description = "统一响应结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    @Schema(description = "状态码，0表示成功，非0为错误码", example = "0")
    private int code;

    @Schema(description = "提示信息", example = "success")
    private String msg;

    @Schema(description = "业务数据")
    private T data;

    @Schema(description = "链路追踪 ID，便于排查问题")
    private String traceId;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        Result<T> result = new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
        result.fillTraceId();
        return result;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>(code, msg, null);
        result.fillTraceId();
        return result;
    }

    /**
     * 从 MDC 读取 traceId 填入响应，用于错误响应时让前端能拿到链路 ID。
     */
    private void fillTraceId() {
        this.traceId = org.slf4j.MDC.get("traceId");
    }
}
