package com.flashsale.order.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO implements Serializable {
    private Long id;
    private Long userId;
    private Long seckillGoodsId;
    private Long goodsId;
    private BigDecimal orderPrice;
    private Integer status; // 0-待支付 1-已支付 2-已取消
    private LocalDateTime createTime;
}
