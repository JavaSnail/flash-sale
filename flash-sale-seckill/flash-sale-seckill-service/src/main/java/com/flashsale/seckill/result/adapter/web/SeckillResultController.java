package com.flashsale.seckill.result.adapter.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.api.dto.SeckillResultDTO;
import com.flashsale.seckill.result.application.SeckillResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "秒杀结果", description = "秒杀结果查询接口")
@RestController
@RequestMapping("/seckill/result")
@RequiredArgsConstructor
public class SeckillResultController {

    private final SeckillResultService resultService;

    @Operation(summary = "查询秒杀结果", description = "轮询秒杀结果，返回排队中/成功/失败状态")
    @GetMapping
    public Result<SeckillResultDTO> getResult(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "秒杀商品ID") @RequestParam Long seckillGoodsId) {
        return Result.success(resultService.getResult(userId, seckillGoodsId));
    }
}
