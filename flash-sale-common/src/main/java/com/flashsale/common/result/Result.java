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

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
