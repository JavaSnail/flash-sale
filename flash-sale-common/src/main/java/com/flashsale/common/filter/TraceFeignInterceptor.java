package com.flashsale.common.filter;

import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign 拦截器：将 MDC 中的 traceId 传播到下游服务的请求头 X-Trace-Id。
 */
@Configuration
public class TraceFeignInterceptor implements RequestInterceptor {

    private static final String TRACE_ID = "traceId";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    public void apply(RequestTemplate template) {
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null && !traceId.isBlank()) {
            template.header(HEADER_TRACE_ID, traceId);
        }
    }
}
