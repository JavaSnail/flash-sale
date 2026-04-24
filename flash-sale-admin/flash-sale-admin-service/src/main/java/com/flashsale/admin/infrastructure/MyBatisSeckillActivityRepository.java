package com.flashsale.admin.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.flashsale.admin.domain.ActivityStatus;
import com.flashsale.admin.domain.Money;
import com.flashsale.admin.domain.SeckillActivity;
import com.flashsale.admin.domain.SeckillActivityRepository;
import com.flashsale.admin.domain.TimeRange;
import com.flashsale.admin.infrastructure.mapper.SeckillActivityMapper;

import lombok.RequiredArgsConstructor;

/**
 * 秒杀活动仓储 MyBatis 实现。
 */
@Repository
@RequiredArgsConstructor
public class MyBatisSeckillActivityRepository implements SeckillActivityRepository {

    private final SeckillActivityMapper mapper;

    @Override
    public SeckillActivity save(SeckillActivity activity) {
        SeckillActivityDO d = toDO(activity);
        if (activity.getId() == null) {
            mapper.insert(d);
            return activity.withId(d.getId());
        }
        else {
            mapper.updateById(d);
            return activity;
        }
    }

    @Override
    public Optional<SeckillActivity> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SeckillActivity> findAll() {
        return mapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, int status) {
        mapper.updateStatus(id, status);
    }

    // ==================== DO ↔ Domain 转换 ====================

    /**
     * 数据对象 → 领域对象。 将 Integer status 转换为 {@link ActivityStatus} 枚举， 将 startTime/endTime 组合为 {@link TimeRange}。
     */
    private SeckillActivity toDomain(SeckillActivityDO d) {
        return SeckillActivity.reconstitute(d.getId(), d.getActivityName(), d.getGoodsId(),
            Money.of(d.getSeckillPrice()), d.getStockCount() == null ? 0 : d.getStockCount(),
            new TimeRange(d.getStartTime(), d.getEndTime()),
            ActivityStatus.fromCode(d.getStatus() == null ? 0 : d.getStatus()), d.getCreateTime(), d.getUpdateTime());
    }

    /**
     * 领域对象 → 数据对象。 将 {@link ActivityStatus} 枚举转换为 Integer code， 将 {@link TimeRange} 拆分为 startTime/endTime。
     */
    private SeckillActivityDO toDO(SeckillActivity a) {
        SeckillActivityDO d = new SeckillActivityDO();
        d.setId(a.getId());
        d.setActivityName(a.getActivityName());
        d.setGoodsId(a.getGoodsId());
        d.setSeckillPrice(a.getSeckillPrice().amount());
        d.setStockCount(a.getStockCount());
        d.setStartTime(a.getStartTime());
        d.setEndTime(a.getEndTime());
        d.setStatus(a.getStatus().code());
        return d;
    }
}
