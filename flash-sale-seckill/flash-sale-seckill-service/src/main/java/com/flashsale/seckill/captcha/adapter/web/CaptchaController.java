package com.flashsale.seckill.captcha.adapter.web;

import com.flashsale.seckill.captcha.application.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

@Tag(name = "验证码", description = "秒杀验证码生成接口")
@RestController
@RequestMapping("/seckill/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @Operation(summary = "获取验证码", description = "生成秒杀验证码图片")
    @GetMapping
    public void getCaptcha(@Parameter(description = "用户ID") @RequestParam("userId") Long userId,
                           @Parameter(description = "秒杀商品ID") @RequestParam("seckillGoodsId") Long seckillGoodsId,
                           HttpServletResponse response) throws Exception {
        BufferedImage image = captchaService.generateCaptcha(userId, seckillGoodsId);
        response.setContentType("image/jpeg");
        try (OutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "JPEG", out);
        }
    }
}
