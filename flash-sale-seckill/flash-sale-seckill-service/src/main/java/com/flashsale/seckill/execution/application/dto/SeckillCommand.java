package com.flashsale.seckill.execution.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "秒杀执行命令")
@Data
public class SeckillCommand {

    @Schema(description = "用户ID", example = "1")
    @NotNull
    private Long userId;

    @Schema(description = "秒杀商品ID", example = "1")
    @NotNull
    private Long seckillGoodsId;

    @Schema(description = "秒杀令牌")
    @NotNull
    private String token;
}
