package com.flashsale.goods.domain;

import java.util.List;
import java.util.Optional;

public interface SeckillGoodsRepository {
    Optional<SeckillGoods> findById(Long id);
    List<SeckillGoods> findAll();
    void save(SeckillGoods seckillGoods);
    boolean decreaseStock(Long id);
    void increaseStock(Long id);
}
