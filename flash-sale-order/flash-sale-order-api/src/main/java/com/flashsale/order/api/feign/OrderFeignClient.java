package com.flashsale.order.api.feign;

import java.util.List;

import com.flashsale.common.result.Result;
import com.flashsale.order.api.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flash-sale-order", path = "/order")
public interface OrderFeignClient {

    @GetMapping("/list")
    Result<List<OrderDTO>> listOrders();

    @GetMapping("/{id}")
    Result<OrderDTO> getById(@PathVariable("id") Long id);
}
