package com.flashsale.seckill.execution.infrastructure;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.flashsale.seckill.execution.domain.SeckillUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisSeckillUserRepository implements SeckillUserRepository {

    private final StringRedisTemplate redisTemplate;

    private String key(Long userId, Long seckillGoodsId) {
        return "seckill:user:" + userId + ":" + seckillGoodsId;
    }

    @Override
    public boolean tryMarkSeckilled(Long userId, Long seckillGoodsId) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key(userId, seckillGoodsId), "1", 24, TimeUnit.HOURS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void removeMark(Long userId, Long seckillGoodsId) {
        redisTemplate.delete(key(userId, seckillGoodsId));
    }
}
