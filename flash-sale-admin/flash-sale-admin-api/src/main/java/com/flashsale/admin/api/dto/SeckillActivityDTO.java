package com.flashsale.admin.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "秒杀活动信息")
@Data
public class SeckillActivityDTO implements Serializable {
    @Schema(description = "活动ID", example = "1")
    private Long id;

    @Schema(description = "活动名称", example = "618大促秒杀")
    private String activityName;

    @Schema(description = "商品ID", example = "100")
    private Long goodsId;

    @Schema(description = "秒杀价格", example = "4999.00")
    private BigDecimal seckillPrice;

    @Schema(description = "库存数量", example = "100")
    private Integer stockCount;

    @Schema(description = "活动开始时间")
    private LocalDateTime startTime;

    @Schema(description = "活动结束时间")
    private LocalDateTime endTime;

    @Schema(description = "活动状态：0-未开始 1-进行中 2-已结束", example = "0")
    private Integer status;
}
