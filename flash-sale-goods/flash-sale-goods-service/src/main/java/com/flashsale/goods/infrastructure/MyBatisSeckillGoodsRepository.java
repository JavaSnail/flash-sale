package com.flashsale.goods.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.flashsale.goods.domain.Money;
import com.flashsale.goods.domain.SeckillGoods;
import com.flashsale.goods.domain.SeckillGoodsRepository;
import com.flashsale.goods.domain.TimeRange;
import com.flashsale.goods.infrastructure.mapper.SeckillGoodsMapper;

import lombok.RequiredArgsConstructor;

/**
 * 秒杀商品仓储 MyBatis 实现。
 * <p>
 * 库存扣减/回滚委托给 Mapper 的自定义 SQL（{@code UPDATE ... WHERE stock > 0}）， 保证数据库层面的原子性防超卖。
 * </p>
 */
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
    public SeckillGoods save(SeckillGoods sg) {
        SeckillGoodsDO d = toDO(sg);
        if (sg.getId() == null) {
            mapper.insert(d);
            return sg.withId(d.getId());
        }
        else {
            mapper.updateById(d);
            return sg;
        }
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public boolean decreaseStock(Long id) {
        return mapper.decreaseStock(id) > 0;
    }

    @Override
    public void increaseStock(Long id) {
        mapper.increaseStock(id);
    }

    // ==================== DO ↔ Domain 转换 ====================

    /**
     * 数据对象 → 领域对象。将扁平的 startTime/endTime 组合为 {@link TimeRange} 值对象。
     */
    private SeckillGoods toDomain(SeckillGoodsDO d) {
        return SeckillGoods.reconstitute(d.getId(), d.getGoodsId(), Money.of(d.getSeckillPrice()),
            d.getStockCount() == null ? 0 : d.getStockCount(), new TimeRange(d.getStartTime(), d.getEndTime()),
            d.getCreateTime());
    }

    /**
     * 领域对象 → 数据对象。将 {@link TimeRange} 拆分为扁平的 startTime/endTime。
     */
    private SeckillGoodsDO toDO(SeckillGoods sg) {
        SeckillGoodsDO d = new SeckillGoodsDO();
        d.setId(sg.getId());
        d.setGoodsId(sg.getGoodsId());
        d.setSeckillPrice(sg.getSeckillPrice().amount());
        d.setStockCount(sg.getStockCount());
        d.setStartTime(sg.getStartTime());
        d.setEndTime(sg.getEndTime());
        return d;
    }
}
