package com.flashsale.seckill.api.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SeckillRequestDTO implements Serializable {

    private Long userId;

    private Long seckillGoodsId;

    private String captcha;

    private String token;
}
