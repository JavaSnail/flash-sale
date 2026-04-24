package com.flashsale.order.infrastructure;

import com.flashsale.order.domain.Order;
import com.flashsale.order.domain.OrderRepository;
import com.flashsale.order.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisOrderRepository implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public void save(Order order) {
        OrderDO orderDO = toDO(order);
        if (order.getId() == null) {
            orderMapper.insert(orderDO);
            order.setId(orderDO.getId());
        } else {
            orderMapper.updateById(orderDO);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public void updateStatus(Long id, int status) {
        orderMapper.updateStatus(id, status);
    }

    private Order toDomain(OrderDO d) {
        Order o = new Order();
        o.setId(d.getId());
        o.setUserId(d.getUserId());
        o.setSeckillGoodsId(d.getSeckillGoodsId());
        o.setGoodsId(d.getGoodsId());
        o.setOrderPrice(d.getOrderPrice());
        o.setStatus(d.getStatus());
        o.setCreateTime(d.getCreateTime());
        o.setUpdateTime(d.getUpdateTime());
        return o;
    }

    private OrderDO toDO(Order o) {
        OrderDO d = new OrderDO();
        d.setId(o.getId());
        d.setUserId(o.getUserId());
        d.setSeckillGoodsId(o.getSeckillGoodsId());
        d.setGoodsId(o.getGoodsId());
        d.setOrderPrice(o.getOrderPrice());
        d.setStatus(o.getStatus());
        return d;
    }
}
