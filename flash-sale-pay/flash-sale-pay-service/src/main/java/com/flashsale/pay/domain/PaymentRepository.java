package com.flashsale.pay.domain;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findByOrderId(Long orderId);
    void updateStatus(Long id, int status, String tradeNo);
}
