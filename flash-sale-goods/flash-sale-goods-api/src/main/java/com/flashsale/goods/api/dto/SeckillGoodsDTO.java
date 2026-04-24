package com.flashsale.goods.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SeckillGoodsDTO implements Serializable {
    private Long id;

    private Long goodsId;

    private String goodsName;

    private String goodsImg;

    private BigDecimal goodsPrice;

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
