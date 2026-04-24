package com.flashsale.order.application;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.goods.api.dto.SeckillGoodsDTO;
import com.flashsale.goods.api.feign.GoodsFeignClient;
import com.flashsale.order.api.dto.OrderDTO;
import com.flashsale.order.domain.Order;
import com.flashsale.order.domain.OrderRepository;
import com.flashsale.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 订单应用服务。
 *
 * <p>编排秒杀订单创建、查询、取消等用例。
 * 订单创建由 MQ 消费者触发，内部包含幂等检查、Feign 查询商品信息、
 * 写入 Redis 轮询结果等编排逻辑。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GoodsFeignClient goodsFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final IdempotentService idempotentService;

    /**
     * 创建秒杀订单。
     *
     * <p>完整流程：</p>
     * <ol>
     *   <li>幂等检查（防重复下单）</li>
     *   <li>通过 Feign 获取秒杀商品信息</li>
     *   <li>领域工厂创建订单聚合根</li>
     *   <li>持久化订单</li>
     *   <li>写入 Redis 供前端轮询秒杀结果</li>
     * </ol>
     *
     * @param userId         用户 ID
     * @param seckillGoodsId 秒杀商品 ID
     * @return 订单 ID，重复下单返回 null
     */
    @Transactional
    public Long createSeckillOrder(Long userId, Long seckillGoodsId) {
        // 1. 幂等检查（防重复下单）
        String idempotentKey = userId + ":" + seckillGoodsId;
        if (!idempotentService.tryAcquire(idempotentKey)) {
            log.warn("Duplicate seckill order: userId={}, goodsId={}", userId, seckillGoodsId);
            return null;
        }

        try {
            // 2. 通过 Feign 获取秒杀商品信息（跨服务查询）
            SeckillGoodsDTO goods = goodsFeignClient.getSeckillGoods(seckillGoodsId).getData();
            if (goods == null) {
                throw new BizException(ErrorCode.NOT_FOUND);
            }

            // 3. 领域工厂创建订单 → 持久化
            Order order = Order.createSeckillOrder(
                    userId, seckillGoodsId, goods.getGoodsId(), goods.getSeckillPrice());
            order = orderRepository.save(order);

            // 4. 写入 Redis 供前端轮询秒杀结果
            String resultKey = "seckill:result:" + userId + ":" + seckillGoodsId;
            redisTemplate.opsForValue().set(resultKey, order.getId().toString(), 24, TimeUnit.HOURS);

            log.info("Seckill order created: orderId={}, userId={}, goodsId={}",
                    order.getId(), userId, seckillGoodsId);
            return order.getId();
        } catch (Exception e) {
            // 写入失败结果供前端感知
            String resultKey = "seckill:result:" + userId + ":" + seckillGoodsId;
            redisTemplate.opsForValue().set(resultKey, "fail:" + e.getMessage(), 24, TimeUnit.HOURS);
            throw e;
        }
    }

    /**
     * 根据 ID 查询订单。
     *
     * @param id 订单 ID
     * @return 订单 DTO
     * @throws BizException 订单不存在
     */
    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_EXIST));
        return toDTO(order);
    }

    /**
     * 取消订单。
     *
     * <p>仅待支付状态的订单可以取消。取消后通过聚合根的 {@link Order#cancel()}
     * 方法完成状态转换（含状态机守卫校验）。</p>
     *
     * @param orderId 订单 ID
     * @throws BizException 订单不存在
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(ErrorCode.ORDER_NOT_EXIST));

        if (order.isPending()) {
            order.cancel();
            orderRepository.updateStatus(orderId, OrderStatus.CANCELLED.code());
            log.info("Order cancelled: orderId={}", orderId);
            // TODO: rollback stock via MQ
        }
    }

    // ==================== DTO 转换 ====================

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setSeckillGoodsId(order.getSeckillGoodsId());
        dto.setGoodsId(order.getGoodsId());
        dto.setOrderPrice(order.getOrderPrice());
        dto.setStatus(order.getStatus().code());
        dto.setCreateTime(order.getCreateTime());
        return dto;
    }
}
