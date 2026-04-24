package com.flashsale.pay.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class PayRequestDTO implements Serializable {

    private Long orderId;

    private Long userId;

    private BigDecimal amount;

    private String payChannel; // ALIPAY, WECHAT
}
