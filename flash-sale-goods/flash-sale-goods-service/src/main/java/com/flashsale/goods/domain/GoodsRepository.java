package com.flashsale.goods.domain;

import java.util.List;
import java.util.Optional;

/**
 * 商品仓储接口（领域层定义）。
 */
public interface GoodsRepository {

    /**
     * 根据 ID 查找商品。
     *
     * @param id 商品 ID
     * @return 商品聚合根，不存在返回 empty
     */
    Optional<Goods> findById(Long id);

    /**
     * 查询全部商品。
     */
    List<Goods> findAll();

    /**
     * 持久化商品。新建时返回带 ID 的新实例，更新时返回原实例。
     *
     * @param goods 商品聚合根
     * @return 持久化后的商品
     */
    Goods save(Goods goods);
}
