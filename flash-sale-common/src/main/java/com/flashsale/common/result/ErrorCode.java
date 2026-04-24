package com.flashsale.common.result;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(0, "success"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),
    SECKILL_OVER(5001, "秒杀结束"),
    REPEAT_SECKILL(5002, "重复秒杀"),
    SECKILL_FAIL(5003, "秒杀失败"),
    CAPTCHA_ERROR(5004, "验证码错误"),
    TOKEN_INVALID(5005, "令牌无效"),
    ACCESS_LIMIT(5006, "访问过于频繁"),
    ORDER_NOT_EXIST(6001, "订单不存在"),
    ORDER_TIMEOUT(6002, "订单超时"),
    PAY_FAIL(7001, "支付失败");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
