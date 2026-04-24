package com.flashsale.seckill.result.domain;

import java.util.Optional;

public interface SeckillResultRepository {
    void saveSuccess(Long userId, Long seckillGoodsId, Long orderId);
    void saveFail(Long userId, Long seckillGoodsId, String reason);
    Optional<String> find(Long userId, Long seckillGoodsId);
}
