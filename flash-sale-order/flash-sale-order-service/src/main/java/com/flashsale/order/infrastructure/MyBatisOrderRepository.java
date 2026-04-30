package com.flashsale.order.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.flashsale.order.domain.Order;
import com.flashsale.order.domain.OrderRepository;
import com.flashsale.order.domain.OrderStatus;
import com.flashsale.order.infrastructure.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

/**
 * 订单仓储 MyBatis 实现。
 */
@Repository
@RequiredArgsConstructor
public class MyBatisOrderRepository implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderDO orderDO = toDO(order);
        if (order.getId() == null) {
            orderMapper.insert(orderDO);
            return order.withId(orderDO.getId());
        }
        else {
            orderMapper.updateById(orderDO);
            return order;
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return orderMapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, int status) {
        orderMapper.updateStatus(id, status);
    }

    // ==================== DO ↔ Domain 转换 ====================

    /**
     * 数据对象 → 领域对象。将 Integer status 转换为 {@link OrderStatus} 枚举。
     */
    private Order toDomain(OrderDO d) {
        return Order.reconstitute(d.getId(), d.getUserId(), d.getSeckillGoodsId(), d.getGoodsId(), d.getOrderPrice(),
            OrderStatus.fromCode(d.getStatus() == null ? 0 : d.getStatus()), d.getCreateTime(), d.getUpdateTime());
    }

    /**
     * 领域对象 → 数据对象。将 {@link OrderStatus} 枚举转换为 Integer code。
     */
    private OrderDO toDO(Order o) {
        OrderDO d = new OrderDO();
        d.setId(o.getId());
        d.setUserId(o.getUserId());
        d.setSeckillGoodsId(o.getSeckillGoodsId());
        d.setGoodsId(o.getGoodsId());
        d.setOrderPrice(o.getOrderPrice());
        d.setStatus(o.getStatus().code());
        return d;
    }
}
