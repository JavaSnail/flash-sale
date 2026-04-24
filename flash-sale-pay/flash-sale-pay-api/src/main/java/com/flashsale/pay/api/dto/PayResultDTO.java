package com.flashsale.pay.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PayResultDTO implements Serializable {
    private Long payId;
    private Long orderId;
    private Integer status; // 0-待支付 1-成功 2-失败
    private String tradeNo;
}
