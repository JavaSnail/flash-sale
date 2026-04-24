package com.flashsale.seckill.execution.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.annotation.AccessLimit;
import com.flashsale.common.result.Result;
import com.flashsale.seckill.execution.application.SeckillExecutionService;
import com.flashsale.seckill.execution.application.dto.SeckillCommand;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillExecutionService executionService;

    @AccessLimit(seconds = 5, maxCount = 1, needLogin = true)
    @PostMapping("/execute")
    public Result<Void> execute(@Valid @RequestBody SeckillCommand command) {
        executionService.execute(command);
        return Result.success();
    }
}
