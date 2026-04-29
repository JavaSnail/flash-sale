package com.flashsale.seckill.api.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "秒杀请求")
@Data
public class SeckillRequestDTO implements Serializable {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "秒杀商品ID", example = "1")
    private Long seckillGoodsId;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "秒杀令牌")
    private String token;
}
