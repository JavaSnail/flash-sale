package com.flashsale.goods.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "商品信息")
@Data
public class GoodsDTO implements Serializable {
    @Schema(description = "商品ID", example = "1")
    private Long id;

    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String goodsName;

    @Schema(description = "商品图片URL")
    private String goodsImg;

    @Schema(description = "商品价格", example = "7999.00")
    private BigDecimal goodsPrice;

    @Schema(description = "库存数量", example = "1000")
    private Integer goodsStock;
}
