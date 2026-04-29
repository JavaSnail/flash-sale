package com.flashsale.gateway.filter;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Gateway 全局过滤器：生成 traceId 并注入到请求头 X-Trace-Id 传递给下游服务，
 * 同时在响应头中回写 X-Trace-Id。
 */
@Component
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_TRACE_ID, traceId)
                .build();

        exchange.getResponse().getHeaders().set(HEADER_TRACE_ID, traceId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
