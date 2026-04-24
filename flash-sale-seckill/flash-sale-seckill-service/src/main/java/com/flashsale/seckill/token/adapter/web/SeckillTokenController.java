package com.flashsale.seckill.token.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.token.application.SeckillTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/seckill/token")
@RequiredArgsConstructor
public class SeckillTokenController {

    private final SeckillTokenService tokenService;

    @PostMapping
    public Result<String> createToken(@RequestParam Long userId, @RequestParam Long seckillGoodsId,
        @RequestParam int captcha) {
        return Result.success(tokenService.createToken(userId, seckillGoodsId, captcha));
    }
}
