package com.learnspring.webfluxtaskmanagerapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestIdWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String reqId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (reqId == null || reqId.isBlank()) reqId = UUID.randomUUID().toString();

        log.debug("Incoming {} {} reqId={}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), reqId);
        // Put into Reactor Context so it travels with the reactive pipeline
        String finalReqId = reqId;
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("requestId", finalReqId));
    }
}
