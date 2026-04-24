package com.flashsale.seckill.execution.domain;

public interface SeckillUserRepository {
    /**
     * Check if user already seckilled this goods. If not, mark it.
     * @return true if this is a new seckill (not duplicate)
     */
    boolean tryMarkSeckilled(Long userId, Long seckillGoodsId);

    void removeMark(Long userId, Long seckillGoodsId);
}
