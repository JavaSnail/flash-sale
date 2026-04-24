package com.flashsale.goods.domain;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Optional<Goods> findById(Long id);
    List<Goods> findAll();
    void save(Goods goods);
}
