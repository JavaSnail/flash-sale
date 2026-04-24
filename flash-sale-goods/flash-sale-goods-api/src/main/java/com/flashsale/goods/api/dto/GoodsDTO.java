package com.flashsale.goods.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GoodsDTO implements Serializable {
    private Long id;
    private String goodsName;
    private String goodsImg;
    private BigDecimal goodsPrice;
    private Integer goodsStock;
}
