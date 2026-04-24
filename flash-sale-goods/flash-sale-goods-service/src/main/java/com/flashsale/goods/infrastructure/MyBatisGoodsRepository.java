package com.flashsale.goods.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.flashsale.goods.domain.Goods;
import com.flashsale.goods.domain.GoodsRepository;
import com.flashsale.goods.domain.Money;
import com.flashsale.goods.infrastructure.mapper.GoodsMapper;

import lombok.RequiredArgsConstructor;

/**
 * 商品仓储 MyBatis 实现。
 */
@Repository
@RequiredArgsConstructor
public class MyBatisGoodsRepository implements GoodsRepository {

    private final GoodsMapper goodsMapper;

    @Override
    public Optional<Goods> findById(Long id) {
        return Optional.ofNullable(goodsMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Goods> findAll() {
        return goodsMapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Goods save(Goods goods) {
        GoodsDO goodsDO = toDO(goods);
        if (goods.getId() == null) {
            goodsMapper.insert(goodsDO);
            return goods.withId(goodsDO.getId());
        }
        else {
            goodsMapper.updateById(goodsDO);
            return goods;
        }
    }

    // ==================== DO ↔ Domain 转换 ====================

    private Goods toDomain(GoodsDO d) {
        return Goods.reconstitute(d.getId(), d.getGoodsName(), d.getGoodsImg(), Money.of(d.getGoodsPrice()),
            d.getGoodsStock() == null ? 0 : d.getGoodsStock(), d.getCreateTime(), d.getUpdateTime());
    }

    private GoodsDO toDO(Goods g) {
        GoodsDO d = new GoodsDO();
        d.setId(g.getId());
        d.setGoodsName(g.getGoodsName());
        d.setGoodsImg(g.getGoodsImg());
        d.setGoodsPrice(g.getGoodsPrice().amount());
        d.setGoodsStock(g.getGoodsStock());
        return d;
    }
}
