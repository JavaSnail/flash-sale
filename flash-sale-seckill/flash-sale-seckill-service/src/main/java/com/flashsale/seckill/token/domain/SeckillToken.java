package com.flashsale.seckill.token.domain;

import java.util.UUID;

public class SeckillToken {

    private final String token;

    private final Long userId;

    private final Long seckillGoodsId;

    public SeckillToken(Long userId, Long seckillGoodsId) {
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeckillGoodsId() {
        return seckillGoodsId;
    }
}
