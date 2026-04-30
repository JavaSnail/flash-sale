package com.flashsale.pay.domain;

import java.util.List;
import java.util.Optional;

/**
 * 支付仓储接口（领域层定义）。
 */
public interface PaymentRepository {

    /**
     * 持久化支付记录。新建时返回带 ID 的新实例。
     */
    Payment save(Payment payment);

    /**
     * 根据订单 ID 查找支付记录。
     *
     * @param orderId 订单 ID
     * @return 支付记录，不存在返回 empty
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * 查询所有支付记录。
     */
    List<Payment> findAll();

    /**
     * 直接更新支付状态和交易号（数据库层操作）。
     *
     * @param id 支付 ID
     * @param status 目标状态码（使用 {@link PaymentStatus#code()}）
     * @param tradeNo 第三方交易号（失败时为 null）
     */
    void updateStatus(Long id, int status, String tradeNo);
}
