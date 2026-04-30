package com.flashsale.goods.domain;

import java.util.List;
import java.util.Optional;

/**
 * 秒杀商品仓储接口（领域层定义）。
 * <p>
 * 除基本 CRUD 外，提供数据库层面的原子库存操作 （{@link #decreaseStock} / {@link #increaseStock}）， 配合 SQL
 * {@code UPDATE ... WHERE stock > 0} 实现防超卖。
 * </p>
 */
public interface SeckillGoodsRepository {

    Optional<SeckillGoods> findById(Long id);

    List<SeckillGoods> findAll();

    /**
     * 持久化秒杀商品。新建时返回带 ID 的新实例。
     */
    SeckillGoods save(SeckillGoods seckillGoods);

    void deleteById(Long id);

    /**
     * 数据库层原子扣减库存（{@code UPDATE ... SET stock = stock - 1 WHERE stock > 0}）。
     *
     * @param id 秒杀商品 ID
     * @return 扣减成功（受影响行数 > 0）返回 true，库存不足返回 false
     */
    boolean decreaseStock(Long id);

    /**
     * 回滚库存（订单取消等场景）。
     *
     * @param id 秒杀商品 ID
     */
    void increaseStock(Long id);
}
