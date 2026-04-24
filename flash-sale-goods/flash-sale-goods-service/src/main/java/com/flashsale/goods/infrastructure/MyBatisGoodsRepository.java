package com.flashsale.goods.infrastructure;

import com.flashsale.goods.domain.Goods;
import com.flashsale.goods.domain.GoodsRepository;
import com.flashsale.goods.infrastructure.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void save(Goods goods) {
        GoodsDO goodsDO = toDO(goods);
        if (goods.getId() == null) {
            goodsMapper.insert(goodsDO);
            goods.setId(goodsDO.getId());
        } else {
            goodsMapper.updateById(goodsDO);
        }
    }

    private Goods toDomain(GoodsDO d) {
        Goods g = new Goods();
        g.setId(d.getId());
        g.setGoodsName(d.getGoodsName());
        g.setGoodsImg(d.getGoodsImg());
        g.setGoodsPrice(d.getGoodsPrice());
        g.setGoodsStock(d.getGoodsStock());
        g.setCreateTime(d.getCreateTime());
        g.setUpdateTime(d.getUpdateTime());
        return g;
    }

    private GoodsDO toDO(Goods g) {
        GoodsDO d = new GoodsDO();
        d.setId(g.getId());
        d.setGoodsName(g.getGoodsName());
        d.setGoodsImg(g.getGoodsImg());
        d.setGoodsPrice(g.getGoodsPrice());
        d.setGoodsStock(g.getGoodsStock());
        return d;
    }
}
