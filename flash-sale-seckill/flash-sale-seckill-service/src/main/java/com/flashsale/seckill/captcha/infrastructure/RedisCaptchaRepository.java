package com.flashsale.seckill.captcha.infrastructure;

import com.flashsale.seckill.captcha.domain.CaptchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisCaptchaRepository implements CaptchaRepository {

    private final StringRedisTemplate redisTemplate;

    private String key(Long userId, Long seckillGoodsId) {
        return "captcha:" + userId + ":" + seckillGoodsId;
    }

    @Override
    public void save(Long userId, Long seckillGoodsId, int answer, int ttlSeconds) {
        redisTemplate.opsForValue().set(key(userId, seckillGoodsId),
                String.valueOf(answer), ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<Integer> find(Long userId, Long seckillGoodsId) {
        String val = redisTemplate.opsForValue().get(key(userId, seckillGoodsId));
        return val == null ? Optional.empty() : Optional.of(Integer.parseInt(val));
    }

    @Override
    public void remove(Long userId, Long seckillGoodsId) {
        redisTemplate.delete(key(userId, seckillGoodsId));
    }
}
