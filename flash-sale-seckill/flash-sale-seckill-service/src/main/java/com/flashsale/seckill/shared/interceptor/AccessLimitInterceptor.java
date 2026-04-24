package com.flashsale.seckill.shared.interceptor;

import com.flashsale.common.annotation.AccessLimit;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AccessLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
        if (accessLimit == null) {
            return true;
        }

        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();

        String ip = request.getRemoteAddr();
        String key = "access:" + request.getRequestURI() + ":" + ip;

        String countStr = redisTemplate.opsForValue().get(key);
        if (countStr == null) {
            redisTemplate.opsForValue().set(key, "1", seconds, TimeUnit.SECONDS);
        } else if (Integer.parseInt(countStr) < maxCount) {
            redisTemplate.opsForValue().increment(key);
        } else {
            response.setContentType("application/json;charset=UTF-8");
            try (OutputStream out = response.getOutputStream()) {
                out.write(objectMapper.writeValueAsBytes(Result.error(ErrorCode.ACCESS_LIMIT)));
            }
            return false;
        }
        return true;
    }
}
