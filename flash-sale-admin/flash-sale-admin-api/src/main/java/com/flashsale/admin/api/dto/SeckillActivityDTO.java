package com.flashsale.admin.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillActivityDTO implements Serializable {
    private Long id;
    private String activityName;
    private Long goodsId;
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 0-未开始 1-进行中 2-已结束
}
