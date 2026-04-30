package com.flashsale.pay.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "支付结果")
@Data
public class PayResultDTO implements Serializable {

    @Schema(description = "支付单ID", example = "2001")
    private Long payId;

    @Schema(description = "订单ID", example = "1001")
    private Long orderId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "支付金额", example = "99.00")
    private BigDecimal amount;

    @Schema(description = "支付渠道：ALIPAY/WECHAT", example = "ALIPAY")
    private String payChannel;

    @Schema(description = "支付状态：0-待支付 1-成功 2-失败", example = "0")
    private Integer status;

    @Schema(description = "第三方交易号")
    private String tradeNo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
