package com.flashsale.common.result;

import java.io.Serializable;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private int code;

    private String msg;

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
