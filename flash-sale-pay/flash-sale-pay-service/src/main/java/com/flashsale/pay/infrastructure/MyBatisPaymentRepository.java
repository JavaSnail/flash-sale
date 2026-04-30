package com.flashsale.pay.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.pay.domain.PayChannel;
import com.flashsale.pay.domain.Payment;
import com.flashsale.pay.domain.PaymentRepository;
import com.flashsale.pay.domain.PaymentStatus;
import com.flashsale.pay.domain.TradeNo;
import com.flashsale.pay.infrastructure.mapper.PaymentMapper;

import lombok.RequiredArgsConstructor;

/**
 * 支付仓储 MyBatis 实现。
 * <p>
 * 负责 {@link Payment} 领域对象与 {@link PaymentDO} 数据对象之间的转换。 枚举/值对象 ↔ 原始类型的映射在 toDomain / toDO 中完成。
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class MyBatisPaymentRepository implements PaymentRepository {

    private final PaymentMapper mapper;

    @Override
    public Payment save(Payment payment) {
        PaymentDO d = toDO(payment);
        if (payment.getId() == null) {
            mapper.insert(d);
            return payment.withId(d.getId());
        }
        else {
            mapper.updateById(d);
            return payment;
        }
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        PaymentDO d = mapper.selectOne(new LambdaQueryWrapper<PaymentDO>().eq(PaymentDO::getOrderId, orderId));
        return Optional.ofNullable(d).map(this::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return mapper.selectList(null).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, int status, String tradeNo) {
        mapper.updateStatus(id, status, tradeNo);
    }

    // ==================== DO ↔ Domain 转换 ====================

    /**
     * 数据对象 → 领域对象。 将 String payChannel 转换为 {@link PayChannel} 枚举， 将 Integer status 转换为 {@link PaymentStatus} 枚举， 将
     * String tradeNo 包装为 {@link TradeNo} 值对象。
     */
    private Payment toDomain(PaymentDO d) {
        return Payment.reconstitute(d.getId(), d.getOrderId(), d.getUserId(), d.getAmount(),
            PayChannel.of(d.getPayChannel()), PaymentStatus.fromCode(d.getStatus() == null ? 0 : d.getStatus()),
            d.getTradeNo() == null || d.getTradeNo().isBlank() ? null : TradeNo.of(d.getTradeNo()), d.getCreateTime(),
            d.getUpdateTime());
    }

    /**
     * 领域对象 → 数据对象。 将 {@link PayChannel} 枚举转换为 String name， 将 {@link PaymentStatus} 枚举转换为 Integer code， 将
     * {@link TradeNo} 值对象提取为 String。
     */
    private PaymentDO toDO(Payment p) {
        PaymentDO d = new PaymentDO();
        d.setId(p.getId());
        d.setOrderId(p.getOrderId());
        d.setUserId(p.getUserId());
        d.setAmount(p.getAmount());
        d.setPayChannel(p.getPayChannel().name());
        d.setStatus(p.getStatus().code());
        d.setTradeNo(p.getTradeNo() == null ? null : p.getTradeNo().value());
        return d;
    }
}
