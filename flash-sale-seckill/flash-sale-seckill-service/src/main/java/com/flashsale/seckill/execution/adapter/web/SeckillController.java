package com.flashsale.seckill.execution.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.annotation.AccessLimit;
import com.flashsale.common.result.Result;
import com.flashsale.seckill.execution.application.SeckillExecutionService;
import com.flashsale.seckill.execution.application.dto.SeckillCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "秒杀执行", description = "秒杀下单接口")
@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillExecutionService executionService;

    @Operation(summary = "执行秒杀", description = "提交秒杀请求，需要先获取token")
    @AccessLimit(seconds = 5, maxCount = 1, needLogin = true)
    @PostMapping("/execute")
    public Result<Void> execute(@Valid @RequestBody SeckillCommand command) {
        executionService.execute(command);
        return Result.success();
    }
}
