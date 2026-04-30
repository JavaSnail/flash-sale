package com.flashsale.order.domain;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口（领域层定义）。
 */
public interface OrderRepository {

    /**
     * 持久化订单。新建时返回带 ID 的新实例。
     */
    Order save(Order order);

    /**
     * 根据 ID 查找订单。
     */
    Optional<Order> findById(Long id);

    /**
     * 查询所有订单。
     */
    List<Order> findAll();

    /**
     * 直接更新订单状态（数据库层操作）。
     *
     * @param id 订单 ID
     * @param status 目标状态码（使用 {@link OrderStatus#code()}）
     */
    void updateStatus(Long id, int status);
}
