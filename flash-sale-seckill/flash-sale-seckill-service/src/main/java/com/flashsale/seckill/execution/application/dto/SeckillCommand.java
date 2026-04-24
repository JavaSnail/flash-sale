package com.flashsale.seckill.execution.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeckillCommand {

    @NotNull
    private Long userId;

    @NotNull
    private Long seckillGoodsId;

    @NotNull
    private String token;
}
