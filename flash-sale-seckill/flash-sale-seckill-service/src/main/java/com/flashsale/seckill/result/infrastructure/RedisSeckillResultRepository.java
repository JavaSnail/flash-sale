package com.flashsale.seckill.result.infrastructure;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.flashsale.seckill.result.domain.SeckillResultRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisSeckillResultRepository implements SeckillResultRepository {

    private final StringRedisTemplate redisTemplate;

    private String key(Long userId, Long seckillGoodsId) {
        return "seckill:result:" + userId + ":" + seckillGoodsId;
    }

    @Override
    public void saveSuccess(Long userId, Long seckillGoodsId, Long orderId) {
        redisTemplate.opsForValue().set(key(userId, seckillGoodsId), orderId.toString(), 24, TimeUnit.HOURS);
    }

    @Override
    public void saveFail(Long userId, Long seckillGoodsId, String reason) {
        redisTemplate.opsForValue().set(key(userId, seckillGoodsId), "fail:" + reason, 24, TimeUnit.HOURS);
    }

    @Override
    public Optional<String> find(Long userId, Long seckillGoodsId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId, seckillGoodsId)));
    }
}
