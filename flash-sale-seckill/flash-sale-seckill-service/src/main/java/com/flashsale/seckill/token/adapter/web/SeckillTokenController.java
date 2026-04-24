package com.flashsale.seckill.token.adapter.web;

import com.flashsale.common.result.Result;
import com.flashsale.seckill.token.application.SeckillTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill/token")
@RequiredArgsConstructor
public class SeckillTokenController {

    private final SeckillTokenService tokenService;

    @PostMapping
    public Result<String> createToken(@RequestParam Long userId,
                                      @RequestParam Long seckillGoodsId,
                                      @RequestParam int captcha) {
        return Result.success(tokenService.createToken(userId, seckillGoodsId, captcha));
    }
}
