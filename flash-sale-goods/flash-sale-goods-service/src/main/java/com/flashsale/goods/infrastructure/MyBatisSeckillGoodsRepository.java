package com.flashsale.goods.infrastructure;

import com.flashsale.goods.domain.SeckillGoods;
import com.flashsale.goods.domain.SeckillGoodsRepository;
import com.flashsale.goods.infrastructure.mapper.SeckillGoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyBatisSeckillGoodsRepository implements SeckillGoodsRepository {

    private final SeckillGoodsMapper mapper;

    @Override
    public Optional<SeckillGoods> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SeckillGoods> findAll() {
        return mapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void save(SeckillGoods sg) {
        SeckillGoodsDO d = toDO(sg);
        if (sg.getId() == null) {
            mapper.insert(d);
            sg.setId(d.getId());
        } else {
            mapper.updateById(d);
        }
    }

    @Override
    public boolean decreaseStock(Long id) {
        return mapper.decreaseStock(id) > 0;
    }

    @Override
    public void increaseStock(Long id) {
        mapper.increaseStock(id);
    }

    private SeckillGoods toDomain(SeckillGoodsDO d) {
        SeckillGoods sg = new SeckillGoods();
        sg.setId(d.getId());
        sg.setGoodsId(d.getGoodsId());
        sg.setSeckillPrice(d.getSeckillPrice());
        sg.setStockCount(d.getStockCount());
        sg.setStartTime(d.getStartTime());
        sg.setEndTime(d.getEndTime());
        sg.setCreateTime(d.getCreateTime());
        return sg;
    }

    private SeckillGoodsDO toDO(SeckillGoods sg) {
        SeckillGoodsDO d = new SeckillGoodsDO();
        d.setId(sg.getId());
        d.setGoodsId(sg.getGoodsId());
        d.setSeckillPrice(sg.getSeckillPrice());
        d.setStockCount(sg.getStockCount());
        d.setStartTime(sg.getStartTime());
        d.setEndTime(sg.getEndTime());
        return d;
    }
}
