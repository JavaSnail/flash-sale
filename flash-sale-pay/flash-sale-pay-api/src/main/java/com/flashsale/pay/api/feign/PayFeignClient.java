package com.flashsale.pay.api.feign;

import com.flashsale.common.result.Result;
import com.flashsale.pay.api.dto.PayResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flash-sale-pay", path = "/pay")
public interface PayFeignClient {

    @GetMapping("/{orderId}")
    Result<PayResultDTO> getByOrderId(@PathVariable("orderId") Long orderId);
}
