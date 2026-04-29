package com.flashsale.seckill.token.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.token.application.SeckillTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "秒杀令牌", description = "秒杀令牌生成接口")
@RestController
@RequestMapping("/seckill/token")
@RequiredArgsConstructor
public class SeckillTokenController {

    private final SeckillTokenService tokenService;

    @Operation(summary = "获取秒杀令牌", description = "验证验证码后生成秒杀令牌")
    @PostMapping
    public Result<String> createToken(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "秒杀商品ID") @RequestParam Long seckillGoodsId,
            @Parameter(description = "验证码答案") @RequestParam int captcha) {
        return Result.success(tokenService.createToken(userId, seckillGoodsId, captcha));
    }
}
