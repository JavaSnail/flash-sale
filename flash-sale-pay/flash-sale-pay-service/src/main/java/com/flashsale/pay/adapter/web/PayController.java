package com.flashsale.pay.adapter.web;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @PostMapping("/create")
    public Result<Long> createPayment(@Valid @RequestBody PayRequestDTO request) {
        return Result.success(payService.createPayment(request));
    }

    @PostMapping("/callback")
    public Result<Void> callback(@RequestParam Long orderId, @RequestParam String tradeNo,
        @RequestParam boolean success) {
        payService.handlePayCallback(orderId, tradeNo, success);
        return Result.success();
    }

    @GetMapping("/{orderId}")
    public Result<PayResultDTO> getByOrderId(@PathVariable Long orderId) {
        return Result.success(payService.getByOrderId(orderId));
    }
}
