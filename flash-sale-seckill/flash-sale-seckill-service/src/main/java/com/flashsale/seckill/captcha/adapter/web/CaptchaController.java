package com.flashsale.seckill.captcha.adapter.web;

import com.flashsale.seckill.captcha.application.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

@RestController
@RequestMapping("/seckill/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public void getCaptcha(@RequestParam Long userId,
                           @RequestParam Long seckillGoodsId,
                           HttpServletResponse response) throws Exception {
        BufferedImage image = captchaService.generateCaptcha(userId, seckillGoodsId);
        response.setContentType("image/jpeg");
        try (OutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "JPEG", out);
        }
    }
}
