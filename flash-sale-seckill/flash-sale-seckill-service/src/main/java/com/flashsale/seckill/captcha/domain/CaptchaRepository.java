package com.flashsale.seckill.captcha.domain;

import java.util.Optional;

public interface CaptchaRepository {
    void save(Long userId, Long seckillGoodsId, int answer, int ttlSeconds);
    Optional<Integer> find(Long userId, Long seckillGoodsId);
    void remove(Long userId, Long seckillGoodsId);
}
