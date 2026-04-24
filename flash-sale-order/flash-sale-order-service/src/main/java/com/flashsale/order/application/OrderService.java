package com.flashsale.order.application;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.api.feign.GoodsFeignClient;
import com.flashsale.order.api.dto.OrderDTO;
import com.flashsale.order.domain.Order;
import com.flashsale.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GoodsFeignClient goodsFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final IdempotentService idempotentService;

    @Transactional
    public Long createSeckillOrder(Long userId, Long seckillGoodsId) {
        // Idempotent check
        String idempotentKey = userId + ":" + seckillGoodsId;
        if (!idempotentService.tryAcquire(idempotentKey)) {
            log.warn("Duplicate seckill order: userId={}, goodsId={}", userId, seckillGoodsId);
            return null;
        }

        try {
            // Get goods info via Feign
            SeckillGoodsDTO goods = goodsFeignClient.getSeckillGoods(seckillGoodsId).getData();
            if (goods == null) {
                throw new BizException(ErrorCode.NOT_FOUND);
            }

            // Create order
            Order order = Order.createSeckillOrder(userId, seckillGoodsId, goods.getGoodsId(), goods.getSeckillPrice());
            orderRepository.save(order);

            // Write result to Redis for seckill result polling
            String resultKey = "seckill:result:" + userId + ":" + seckillGoodsId;
            redisTemplate.opsForValue().set(resultKey, order.getId().toString(), 24, TimeUnit.HOURS);

            log.info("Seckill order created: orderId={}, userId={}, goodsId={}", order.getId(), userId, seckillGoodsId);
            return order.getId();
        } catch (Exception e) {
            // Write failure result
            String resultKey = "seckill:result:" + userId + ":" + seckillGoodsId;
            redisTemplate.opsForValue().set(resultKey, "fail:" + e.getMessage(), 24, TimeUnit.HOURS);
            throw e;
        }
    }

    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_EXIST));
        return toDTO(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_EXIST));
        if (order.isPending()) {
            order.cancel();
            orderRepository.updateStatus(orderId, 2);
            log.info("Order cancelled: orderId={}", orderId);
            // TODO: rollback stock via MQ
        }
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setSeckillGoodsId(order.getSeckillGoodsId());
        dto.setGoodsId(order.getGoodsId());
        dto.setOrderPrice(order.getOrderPrice());
        dto.setStatus(order.getStatus());
        dto.setCreateTime(order.getCreateTime());
        return dto;
    }
}
