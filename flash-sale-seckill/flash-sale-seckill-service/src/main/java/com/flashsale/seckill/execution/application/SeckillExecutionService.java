package com.flashsale.seckill.execution.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.seckill.execution.application.dto.SeckillCommand;
import com.flashsale.seckill.execution.domain.SeckillUserRepository;
import com.flashsale.seckill.execution.domain.StockRepository;
import com.flashsale.seckill.execution.domain.event.SeckillOrderEvent;
import com.flashsale.seckill.token.application.SeckillTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillExecutionService {

    private final StockRepository stockRepository;

    private final SeckillUserRepository seckillUserRepository;

    private final SeckillTokenService tokenService;

    private final SeckillMessageSender messageSender;

    // Local memory flag for sold-out goods (reduces Redis access)
    private final Map<Long, Boolean> localSoldOutMap = new ConcurrentHashMap<>();

    public void execute(SeckillCommand command) {
        Long userId = command.getUserId();
        Long seckillGoodsId = command.getSeckillGoodsId();

        // 1. Validate token (one-time use)
        if (!tokenService.validateToken(userId, seckillGoodsId, command.getToken())) {
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }

        // 2. Local memory check - sold out?
        if (Boolean.TRUE.equals(localSoldOutMap.get(seckillGoodsId))) {
            throw new BizException(ErrorCode.SECKILL_OVER);
        }

        // 3. Redis check - sold out?
        if (stockRepository.isSoldOut(seckillGoodsId)) {
            localSoldOutMap.put(seckillGoodsId, true);
            throw new BizException(ErrorCode.SECKILL_OVER);
        }

        // 4. Duplicate check
        if (!seckillUserRepository.tryMarkSeckilled(userId, seckillGoodsId)) {
            throw new BizException(ErrorCode.REPEAT_SECKILL);
        }

        // 5. Redis atomic decrement stock (Lua script)
        long remaining = stockRepository.decrementStock(seckillGoodsId);
        if (remaining < 0) {
            // Rollback user mark
            seckillUserRepository.removeMark(userId, seckillGoodsId);
            stockRepository.markSoldOut(seckillGoodsId);
            localSoldOutMap.put(seckillGoodsId, true);
            throw new BizException(ErrorCode.SECKILL_OVER);
        }

        // 6. Send MQ message for async order creation
        SeckillOrderEvent event = new SeckillOrderEvent(userId, seckillGoodsId);
        messageSender.sendSeckillOrder(event);
        log.info("Seckill queued: userId={}, goodsId={}, remaining={}", userId, seckillGoodsId, remaining);
    }

    /**
     * Called by Pub/Sub listener when another instance marks goods as sold out
     */
    public void onSoldOutBroadcast(Long seckillGoodsId) {
        localSoldOutMap.put(seckillGoodsId, true);
    }
}
