package com.learnspring.webfluxtaskmanagerapp.filters;


import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
@Component
public class RequestCostFilter implements WebFilter {
    private static final Logger log = Logger.getLogger(RequestCostFilter.class.getName());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant start = Instant.now();

        ServerHttpResponse response = exchange.getResponse();

        response.beforeCommit(() -> {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            response.getHeaders().set("X-REQUEST-COST", String.valueOf(durationMs)+"ms");
            return Mono.empty();
        });

        // Continue the filter chain
        return chain.filter(exchange);
    }
}
