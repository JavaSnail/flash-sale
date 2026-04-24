package com.flashsale.seckill.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SeckillRequestDTO implements Serializable {
    private Long userId;
    private Long seckillGoodsId;
    private String captcha;
    private String token;
}
