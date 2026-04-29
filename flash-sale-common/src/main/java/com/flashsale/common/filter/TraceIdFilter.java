package com.flashsale.common.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet Filter：从请求头 X-Trace-Id 读取 traceId，没有则生成 UUID，
 * 放入 MDC 并在响应头中回写，finally 清理 MDC。
 */
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            String traceId = req.getHeader(HEADER_TRACE_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString().replace("-", "");
            }

            MDC.put(TRACE_ID, traceId);
            res.setHeader(HEADER_TRACE_ID, traceId);

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
