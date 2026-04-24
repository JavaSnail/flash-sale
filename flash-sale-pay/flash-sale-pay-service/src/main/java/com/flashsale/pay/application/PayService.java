package com.flashsale.pay.application;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.pay.api.dto.PayRequestDTO;
import com.flashsale.pay.api.dto.PayResultDTO;
import com.flashsale.pay.domain.Payment;
import com.flashsale.pay.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

    private final PaymentRepository paymentRepository;
    private final RocketMQTemplate rocketMQTemplate;

    public Long createPayment(PayRequestDTO request) {
        Payment payment = Payment.create(
                request.getOrderId(), request.getUserId(),
                request.getAmount(), request.getPayChannel());
        paymentRepository.save(payment);
        return payment.getId();
    }

    public void handlePayCallback(Long orderId, String tradeNo, boolean success) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BizException(ErrorCode.PAY_FAIL, "支付记录不存在"));

        if (success) {
            payment.markSuccess(tradeNo);
            paymentRepository.updateStatus(payment.getId(), 1, tradeNo);
            // Send pay notification via RocketMQ transaction message
            String payload = orderId.toString();
            rocketMQTemplate.send(MQConstants.PAY_NOTIFY_TOPIC,
                    MessageBuilder.withPayload(payload).build());
            log.info("Payment success: orderId={}, tradeNo={}", orderId, tradeNo);
        } else {
            payment.markFail();
            paymentRepository.updateStatus(payment.getId(), 2, null);
            log.info("Payment failed: orderId={}", orderId);
        }
    }

    public PayResultDTO getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BizException(ErrorCode.PAY_FAIL, "支付记录不存在"));
        PayResultDTO dto = new PayResultDTO();
        dto.setPayId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setStatus(payment.getStatus());
        dto.setTradeNo(payment.getTradeNo());
        return dto;
    }
}
