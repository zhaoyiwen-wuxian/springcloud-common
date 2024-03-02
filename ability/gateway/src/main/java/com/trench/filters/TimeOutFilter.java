package com.trench.filters;

import com.trench.batis.constant.FilterOrderConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 超时
 */
@Service
public class TimeOutFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 超时单位秒
        return chain.filter(exchange).timeout(Duration.ofSeconds(10));
    }

    @Override
    public int getOrder() {
        return FilterOrderConstant.TIME_OUT_FILTER_ORDER;
    }
}
