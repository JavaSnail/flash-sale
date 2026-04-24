package com.flashsale.seckill.token.domain;

public interface SeckillTokenRepository {
    void save(SeckillToken token, int ttlSeconds);
    boolean validateAndConsume(Long userId, Long seckillGoodsId, String token);
}
