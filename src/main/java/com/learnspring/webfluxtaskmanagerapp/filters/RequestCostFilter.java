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
        String path = exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI().getPath();
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");

        return chain.filter(exchange)
                .doFinally(signal -> {
                    Duration duration = Duration.between(start, Instant.now());
                    long durationMs = duration.toMillis();

                    ServerHttpResponse response = exchange.getResponse();
                    Integer status = response.getStatusCode() != null ? response.getStatusCode().value() : null;

                    System.out.println("Status: " + status);
                    System.out.println("RequestId: " + requestId);
                    System.out.println("Path: " + path);
                    System.out.println("Duration: " + duration);
                    System.out.println("Response: " + response);


                });
    }
}
