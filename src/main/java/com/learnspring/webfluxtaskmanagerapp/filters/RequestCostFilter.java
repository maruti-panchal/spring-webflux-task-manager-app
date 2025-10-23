package com.learnspring.webfluxtaskmanagerapp.filters;


import org.springframework.core.annotation.Order;
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
@Order(1)
public class RequestCostFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/login") || path.equals("/auth/signup")) {
            return chain.filter(exchange); // skip
        }


        Instant start = Instant.now();

        ServerHttpResponse response = exchange.getResponse();

        response.beforeCommit(() -> {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            response.getHeaders().set("X-REQUEST-COST", String.valueOf(durationMs)+"ms");
            System.out.println("Cost Filter : Order 1");
            return Mono.empty();
        });

        // Continue the filter chain

        return chain.filter(exchange);
    }
}
