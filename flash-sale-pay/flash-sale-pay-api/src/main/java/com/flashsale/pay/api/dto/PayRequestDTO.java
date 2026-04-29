package com.flashsale.pay.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "支付请求")
@Data
public class PayRequestDTO implements Serializable {

    @Schema(description = "订单ID", example = "1001")
    private Long orderId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "支付金额", example = "4999.00")
    private BigDecimal amount;

    @Schema(description = "支付渠道：ALIPAY-支付宝 WECHAT-微信", example = "ALIPAY")
    private String payChannel;
}
