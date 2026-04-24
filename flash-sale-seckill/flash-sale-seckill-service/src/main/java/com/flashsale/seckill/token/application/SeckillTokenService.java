package com.flashsale.seckill.token.application;

import org.springframework.stereotype.Service;

import com.flashsale.common.exception.BizException;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.seckill.captcha.application.CaptchaService;
import com.flashsale.seckill.token.domain.SeckillToken;
import com.flashsale.seckill.token.domain.SeckillTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeckillTokenService {

    private final SeckillTokenRepository tokenRepository;

    private final CaptchaService captchaService;

    public String createToken(Long userId, Long seckillGoodsId, int captchaAnswer) {
        // Verify captcha first
        if (!captchaService.verifyCaptcha(userId, seckillGoodsId, captchaAnswer)) {
            throw new BizException(ErrorCode.CAPTCHA_ERROR);
        }
        SeckillToken token = new SeckillToken(userId, seckillGoodsId);
        tokenRepository.save(token, 300);
        return token.getToken();
    }

    public boolean validateToken(Long userId, Long seckillGoodsId, String token) {
        return tokenRepository.validateAndConsume(userId, seckillGoodsId, token);
    }
}
