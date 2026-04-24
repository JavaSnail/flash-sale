package com.flashsale.goods.domain;

/**
 * 领域异常：库存不足。
 *
 * <p>当 {@link SeckillGoods#decrementStock()} 在库存为零时被调用时抛出。</p>
 */
public class StockInsufficientException extends RuntimeException {

    public StockInsufficientException(Long seckillGoodsId) {
        super("库存不足: seckillGoodsId=" + seckillGoodsId);
    }
}
