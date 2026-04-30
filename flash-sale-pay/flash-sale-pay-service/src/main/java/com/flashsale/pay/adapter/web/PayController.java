package com.flashsale.pay.adapter.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.pay.api.dto.PayRequestDTO;
import com.flashsale.pay.api.dto.PayResultDTO;
import com.flashsale.pay.application.PayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "支付管理", description = "支付创建、回调、查询接口")
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @Operation(summary = "创建支付单", description = "根据订单信息创建支付单")
    @PostMapping("/create")
    public Result<Long> createPayment(@Valid @RequestBody PayRequestDTO request) {
        return Result.success(payService.createPayment(request));
    }

    @Operation(summary = "支付回调", description = "第三方支付平台回调通知")
    @PostMapping("/callback")
    public Result<Void> callback(
            @Parameter(description = "订单ID") @RequestParam("orderId") Long orderId,
            @Parameter(description = "第三方交易号") @RequestParam("tradeNo") String tradeNo,
            @Parameter(description = "支付是否成功") @RequestParam("success") boolean success) {
        payService.handlePayCallback(orderId, tradeNo, success);
        return Result.success();
    }

    @Operation(summary = "查询支付列表", description = "获取所有支付记录列表")
    @GetMapping("/list")
    public Result<List<PayResultDTO>> list() {
        return Result.success(payService.listPayments());
    }

    @Operation(summary = "查询支付结果", description = "根据订单ID查询支付结果")
    @GetMapping("/{orderId}")
    public Result<PayResultDTO> getByOrderId(@Parameter(description = "订单ID") @PathVariable("orderId") Long orderId) {
        return Result.success(payService.getByOrderId(orderId));
    }
}
