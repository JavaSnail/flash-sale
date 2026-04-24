package com.flashsale.gateway.filter;

import java.net.InetSocketAddress;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * IP 黑名单全局过滤器。
 * <p>
 * 在网关层拦截请求，检查客户端 IP 是否在 Redis Set {@code gateway:ip:blacklist} 中。 命中黑名单的请求直接返回 403 Forbidden，不再转发至下游服务。
 * </p>
 * <p>
 * 优先级 {@code order = -2}，在所有业务过滤器之前执行，尽早拦截恶意流量。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpBlacklistFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null) {
            return chain.filter(exchange);
        }
        String ip = Objects.requireNonNull(remoteAddress.getAddress()).getHostAddress();

        return redisTemplate.opsForSet().isMember("gateway:ip:blacklist", ip).flatMap(isMember -> {
            if (Boolean.TRUE.equals(isMember)) {
                log.warn("Blocked IP: {}", ip);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
