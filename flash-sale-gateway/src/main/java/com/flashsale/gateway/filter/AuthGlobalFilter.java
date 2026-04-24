package com.flashsale.gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashsale.common.result.ErrorCode;
import com.flashsale.common.result.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器。
 * <p>
 * 在 Spring Cloud Gateway 层拦截所有请求，执行 Token 校验。
 * </p>
 * <h3>处理流程</h3>
 * <ol>
 * <li>白名单放行：{@code /user/login}、{@code /user/register}、{@code /seckill/captcha}</li>
 * <li>非白名单请求：检查 {@code Authorization} Header，缺失则返回 401</li>
 * <li>Token 有效：将 Token 转发至下游微服务（通过 {@code X-User-Token} Header）</li>
 * </ol>
 * <p>
 * 优先级 {@code order = -1}，在 {@link IpBlacklistFilter}（order = -2）之后执行。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITE_LIST = List.of("/user/login", "/user/register", "/seckill/captcha");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Whitelist
        for (String white : WHITE_LIST) {
            if (path.contains(white)) {
                return chain.filter(exchange);
            }
        }

        // Check token header
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isBlank()) {
            return unauthorized(exchange);
        }

        // Forward token to downstream services
        ServerHttpRequest mutatedRequest = request.mutate().header("X-User-Token", token).build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(Result.error(ErrorCode.UNAUTHORIZED));
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
