package com.flashsale.seckill.token.infrastructure;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.flashsale.seckill.token.domain.SeckillToken;
import com.flashsale.seckill.token.domain.SeckillTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisSeckillTokenRepository implements SeckillTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private String key(Long userId, Long seckillGoodsId) {
        return "seckill:token:" + userId + ":" + seckillGoodsId;
    }

    @Override
    public void save(SeckillToken token, int ttlSeconds) {
        redisTemplate.opsForValue().set(key(token.getUserId(), token.getSeckillGoodsId()), token.getToken(), ttlSeconds,
            TimeUnit.SECONDS);
    }

    @Override
    public boolean validateAndConsume(Long userId, Long seckillGoodsId, String token) {
        String key = key(userId, seckillGoodsId);
        String stored = redisTemplate.opsForValue().get(key);
        if (token.equals(stored)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
