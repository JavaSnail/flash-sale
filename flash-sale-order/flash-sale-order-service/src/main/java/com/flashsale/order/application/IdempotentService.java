package com.flashsale.order.application;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 基于 Redis 的幂等性服务。
 * <p>
 * 通过 {@code SET NX} 实现分布式幂等锁，防止同一用户对同一秒杀商品重复下单。 Key 格式：{@code order:idempotent:{userId}:{seckillGoodsId}}，TTL 24
 * 小时后自动过期。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 尝试获取幂等锁。
     * <p>
     * 利用 Redis 的 {@code SET key value NX EX} 原子操作， 首次调用返回 {@code true}（获取成功），后续重复调用返回 {@code false}。
     * </p>
     *
     * @param key 幂等键（通常为 {@code userId:seckillGoodsId}）
     * @return {@code true} 表示首次请求（非重复），{@code false} 表示重复请求
     */
    public boolean tryAcquire(String key) {
        String redisKey = "order:idempotent:" + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);
        return Boolean.TRUE.equals(success);
    }
}
