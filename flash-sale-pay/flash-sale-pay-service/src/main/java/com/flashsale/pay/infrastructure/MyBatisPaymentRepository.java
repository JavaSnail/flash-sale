package com.flashsale.pay.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashsale.pay.domain.Payment;
import com.flashsale.pay.domain.PaymentRepository;
import com.flashsale.pay.infrastructure.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisPaymentRepository implements PaymentRepository {

    private final PaymentMapper mapper;

    @Override
    public void save(Payment payment) {
        PaymentDO d = toDO(payment);
        if (payment.getId() == null) {
            mapper.insert(d);
            payment.setId(d.getId());
        } else {
            mapper.updateById(d);
        }
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        PaymentDO d = mapper.selectOne(
                new LambdaQueryWrapper<PaymentDO>().eq(PaymentDO::getOrderId, orderId));
        return Optional.ofNullable(d).map(this::toDomain);
    }

    @Override
    public void updateStatus(Long id, int status, String tradeNo) {
        mapper.updateStatus(id, status, tradeNo);
    }

    private Payment toDomain(PaymentDO d) {
        Payment p = new Payment();
        p.setId(d.getId());
        p.setOrderId(d.getOrderId());
        p.setUserId(d.getUserId());
        p.setAmount(d.getAmount());
        p.setPayChannel(d.getPayChannel());
        p.setStatus(d.getStatus());
        p.setTradeNo(d.getTradeNo());
        p.setCreateTime(d.getCreateTime());
        p.setUpdateTime(d.getUpdateTime());
        return p;
    }

    private PaymentDO toDO(Payment p) {
        PaymentDO d = new PaymentDO();
        d.setId(p.getId());
        d.setOrderId(p.getOrderId());
        d.setUserId(p.getUserId());
        d.setAmount(p.getAmount());
        d.setPayChannel(p.getPayChannel());
        d.setStatus(p.getStatus());
        d.setTradeNo(p.getTradeNo());
        return d;
    }
}
