package com.flashsale.seckill.execution.infrastructure;

import com.flashsale.seckill.execution.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class RedisStockRepository implements StockRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String SOLDOUT_KEY_PREFIX = "seckill:soldout:";

    // Lua: atomically decrement, return remaining. If key doesn't exist or <= 0, return -1
    private static final String DECR_LUA =
            "local stock = redis.call('get', KEYS[1]) " +
            "if not stock then return -1 end " +
            "stock = tonumber(stock) " +
            "if stock <= 0 then return -1 end " +
            "redis.call('decr', KEYS[1]) " +
            "return stock - 1";

    private static final DefaultRedisScript<Long> DECR_SCRIPT = new DefaultRedisScript<>(DECR_LUA, Long.class);

    @Override
    public long decrementStock(Long seckillGoodsId) {
        Long result = redisTemplate.execute(DECR_SCRIPT,
                Collections.singletonList(STOCK_KEY_PREFIX + seckillGoodsId));
        return result == null ? -1 : result;
    }

    @Override
    public void incrementStock(Long seckillGoodsId) {
        redisTemplate.opsForValue().increment(STOCK_KEY_PREFIX + seckillGoodsId);
    }

    @Override
    public boolean isSoldOut(Long seckillGoodsId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(SOLDOUT_KEY_PREFIX + seckillGoodsId));
    }

    @Override
    public void markSoldOut(Long seckillGoodsId) {
        redisTemplate.opsForValue().set(SOLDOUT_KEY_PREFIX + seckillGoodsId, "1");
        // Broadcast to other instances via Pub/Sub
        redisTemplate.convertAndSend("seckill:soldout:channel", seckillGoodsId.toString());
    }
}
