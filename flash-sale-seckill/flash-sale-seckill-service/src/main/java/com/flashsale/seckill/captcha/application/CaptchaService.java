package com.flashsale.seckill.captcha.application;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.seckill.captcha.domain.Captcha;
import com.flashsale.seckill.captcha.domain.CaptchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository captchaRepository;
    private static final Random RANDOM = new Random();

    public BufferedImage generateCaptcha(Long userId, Long seckillGoodsId) {
        int a = RANDOM.nextInt(10);
        int b = RANDOM.nextInt(10);
        char[] ops = {'+', '-', '*'};
        char op = ops[RANDOM.nextInt(3)];
        int answer = switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            default -> 0;
        };
        String expression = a + " " + op + " " + b + " = ?";
        Captcha captcha = new Captcha(expression, answer);
        captchaRepository.save(userId, seckillGoodsId, captcha.getAnswer(), 300);

        // Generate image
        int width = 130, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(expression, 10, 28);
        g.dispose();
        return image;
    }

    public boolean verifyCaptcha(Long userId, Long seckillGoodsId, int input) {
        Integer answer = captchaRepository.find(userId, seckillGoodsId)
                .orElseThrow(() -> new BizException(ErrorCode.CAPTCHA_ERROR, "验证码已过期"));
        boolean matched = answer == input;
        if (matched) {
            captchaRepository.remove(userId, seckillGoodsId);
        }
        return matched;
    }
}
