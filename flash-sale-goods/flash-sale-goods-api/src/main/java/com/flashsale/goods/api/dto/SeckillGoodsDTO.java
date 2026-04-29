package com.flashsale.goods.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "秒杀商品信息")
@Data
public class SeckillGoodsDTO implements Serializable {
    @Schema(description = "秒杀商品ID", example = "1")
    private Long id;

    @Schema(description = "原商品ID", example = "100")
    private Long goodsId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String goodsName;

    @Schema(description = "商品图片URL")
    private String goodsImg;

    @Schema(description = "原价", example = "7999.00")
    private BigDecimal goodsPrice;

    @Schema(description = "秒杀价", example = "4999.00")
    private BigDecimal seckillPrice;

    @Schema(description = "剩余库存", example = "100")
    private Integer stockCount;

    @Schema(description = "秒杀开始时间")
    private LocalDateTime startTime;

    @Schema(description = "秒杀结束时间")
    private LocalDateTime endTime;
}
