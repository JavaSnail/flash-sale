package com.flashsale.order.adapter.web;

import com.flashsale.common.result.Result;
import com.flashsale.order.api.dto.OrderDTO;
import com.flashsale.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Result<OrderDTO> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }
}
