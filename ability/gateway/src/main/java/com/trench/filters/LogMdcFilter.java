package com.trench.filters;

import org.jboss.logging.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LogMdcFilter implements GlobalFilter, Ordered {
    /**
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = exchange.getRequest().getQueryParams().getFirst("requestId");
        // 将请求方传入的 id 放入 MDC 中
        MDC.put("requestId", requestId);
        return chain.filter(exchange).doFinally(signalType -> {
            // 清除 MDC 中的 requestId，确保不会影响其他请求
            MDC.remove("requestId");
        });
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return -5000;
    }
}
