package com.flashsale.order.domain;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(Long id);
    void updateStatus(Long id, int status);
}
