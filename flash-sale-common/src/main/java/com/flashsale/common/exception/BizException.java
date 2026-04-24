package com.flashsale.common.exception;

import com.flashsale.common.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>封装 {@link ErrorCode}，用于业务规则校验失败时抛出。
 * 全局异常处理器会捕获此异常并转换为标准 {@link com.flashsale.common.result.Result} 响应。</p>
 */
@Getter
public class BizException extends RuntimeException {
    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
