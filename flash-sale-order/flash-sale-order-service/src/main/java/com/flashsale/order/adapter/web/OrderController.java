package com.flashsale.order.adapter.web;

import java.util.List;

import com.flashsale.common.result.Result;
import com.flashsale.order.api.dto.OrderDTO;
import com.flashsale.order.application.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "订单查询接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "查询订单列表", description = "获取所有订单列表")
    @GetMapping("/list")
    public Result<List<OrderDTO>> list() {
        return Result.success(orderService.listOrders());
    }

    @Operation(summary = "根据ID查询订单", description = "根据订单ID获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDTO> getById(@Parameter(description = "订单ID") @PathVariable("id") Long id) {
        return Result.success(orderService.getById(id));
    }
}
