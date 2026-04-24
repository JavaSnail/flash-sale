package com.flashsale.pay.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PayRequestDTO implements Serializable {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String payChannel; // ALIPAY, WECHAT
}
