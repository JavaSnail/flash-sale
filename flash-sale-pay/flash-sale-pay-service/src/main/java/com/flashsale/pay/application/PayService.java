package com.flashsale.pay.application;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.flashsale.common.constant.MQConstants;
import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.pay.api.dto.PayRequestDTO;
import com.flashsale.pay.api.dto.PayResultDTO;
import com.flashsale.pay.domain.PayChannel;
import com.flashsale.pay.domain.Payment;
import com.flashsale.pay.domain.PaymentRepository;
import com.flashsale.pay.domain.PaymentStatus;
import com.flashsale.pay.domain.TradeNo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付应用服务。
 * <p>
 * 编排支付用例：创建支付、处理回调、查询结果。 支付成功后通过 RocketMQ 发送支付通知消息， 由订单服务消费完成订单状态变更。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

    private final PaymentRepository paymentRepository;

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 创建支付记录。
     * <p>
     * 通过 {@link Payment#create} 领域工厂构建聚合根， 支付渠道字符串转换为 {@link PayChannel} 枚举。
     * </p>
     *
     * @param request 支付请求（订单ID、用户ID、金额、渠道）
     * @return 支付记录 ID
     */
    public Long createPayment(PayRequestDTO request) {
        Payment payment = Payment.create(request.getOrderId(), request.getUserId(), request.getAmount(),
            PayChannel.of(request.getPayChannel()));
        payment = paymentRepository.save(payment);
        return payment.getId();
    }

    /**
     * 处理第三方支付回调。
     * <p>
     * 根据回调结果调用聚合根的 {@link Payment#markSuccess} 或 {@link Payment#markFail}（含状态机守卫校验）。 支付成功后发送 RocketMQ 通知消息。
     * </p>
     *
     * @param orderId 订单 ID
     * @param tradeNo 第三方交易号（成功时必须提供）
     * @param success 支付是否成功
     * @throws BizException 支付记录不存在
     */
    public void handlePayCallback(Long orderId, String tradeNo, boolean success) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BizException(ErrorCode.PAY_FAIL, "支付记录不存在"));

        if (success) {
            // 聚合根状态转换：PENDING → SUCCESS
            payment.markSuccess(TradeNo.of(tradeNo));
            paymentRepository.updateStatus(payment.getId(), PaymentStatus.SUCCESS.code(), tradeNo);

            // 发送支付成功通知（订单服务消费后更新订单状态）
            rocketMQTemplate.send(MQConstants.PAY_NOTIFY_TOPIC, MessageBuilder.withPayload(orderId.toString()).build());
            log.info("Payment success: orderId={}, tradeNo={}", orderId, tradeNo);
        }
        else {
            // 聚合根状态转换：PENDING → FAIL
            payment.markFail();
            paymentRepository.updateStatus(payment.getId(), PaymentStatus.FAIL.code(), null);
            log.info("Payment failed: orderId={}", orderId);
        }
    }

    /**
     * 根据订单 ID 查询支付结果。
     *
     * @param orderId 订单 ID
     * @return 支付结果 DTO
     * @throws BizException 支付记录不存在
     */
    public PayResultDTO getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BizException(ErrorCode.PAY_FAIL, "支付记录不存在"));

        PayResultDTO dto = new PayResultDTO();
        dto.setPayId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setStatus(payment.getStatus().code());
        dto.setTradeNo(payment.getTradeNo() == null ? null : payment.getTradeNo().value());
        return dto;
    }
}
