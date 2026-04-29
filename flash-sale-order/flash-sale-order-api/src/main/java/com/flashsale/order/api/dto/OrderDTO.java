package com.flashsale.order.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "订单信息")
@Data
public class OrderDTO implements Serializable {
    @Schema(description = "订单ID", example = "1001")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "秒杀商品ID", example = "1")
    private Long seckillGoodsId;

    @Schema(description = "原商品ID", example = "100")
    private Long goodsId;

    @Schema(description = "订单金额", example = "4999.00")
    private BigDecimal orderPrice;

    @Schema(description = "订单状态：0-待支付 1-已支付 2-已取消", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
