package com.flashsale.seckill.execution.domain;

public interface StockRepository {
    /**
     * Atomically decrement stock. Returns remaining stock after decrement, or -1 if sold out.
     */
    long decrementStock(Long seckillGoodsId);

    void incrementStock(Long seckillGoodsId);

    boolean isSoldOut(Long seckillGoodsId);

    void markSoldOut(Long seckillGoodsId);
}
