package com.flashsale.pay.api.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "支付结果")
@Data
public class PayResultDTO implements Serializable {

    @Schema(description = "支付单ID", example = "2001")
    private Long payId;

    @Schema(description = "订单ID", example = "1001")
    private Long orderId;

    @Schema(description = "支付状态：0-待支付 1-成功 2-失败", example = "0")
    private Integer status;

    @Schema(description = "第三方交易号")
    private String tradeNo;
}
