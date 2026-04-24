package com.flashsale.admin.infrastructure;

import com.flashsale.admin.domain.SeckillActivity;
import com.flashsale.admin.domain.SeckillActivityRepository;
import com.flashsale.admin.infrastructure.mapper.SeckillActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyBatisSeckillActivityRepository implements SeckillActivityRepository {

    private final SeckillActivityMapper mapper;

    @Override
    public void save(SeckillActivity activity) {
        SeckillActivityDO d = toDO(activity);
        if (activity.getId() == null) {
            mapper.insert(d);
            activity.setId(d.getId());
        } else {
            mapper.updateById(d);
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

    private SeckillActivity toDomain(SeckillActivityDO d) {
        SeckillActivity a = new SeckillActivity();
        a.setId(d.getId());
        a.setActivityName(d.getActivityName());
        a.setGoodsId(d.getGoodsId());
        a.setSeckillPrice(d.getSeckillPrice());
        a.setStockCount(d.getStockCount());
        a.setStartTime(d.getStartTime());
        a.setEndTime(d.getEndTime());
        a.setStatus(d.getStatus());
        a.setCreateTime(d.getCreateTime());
        a.setUpdateTime(d.getUpdateTime());
        return a;
    }

    private SeckillActivityDO toDO(SeckillActivity a) {
        SeckillActivityDO d = new SeckillActivityDO();
        d.setId(a.getId());
        d.setActivityName(a.getActivityName());
        d.setGoodsId(a.getGoodsId());
        d.setSeckillPrice(a.getSeckillPrice());
        d.setStockCount(a.getStockCount());
        d.setStartTime(a.getStartTime());
        d.setEndTime(a.getEndTime());
        d.setStatus(a.getStatus());
        return d;
    }
}
