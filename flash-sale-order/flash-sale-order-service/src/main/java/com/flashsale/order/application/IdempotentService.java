package com.flashsale.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final StringRedisTemplate redisTemplate;

    public boolean tryAcquire(String key) {
        String redisKey = "order:idempotent:" + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);
        return Boolean.TRUE.equals(success);
    }
}
