package com.learnspring.webfluxtaskmanagerapp.security;


import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import com.learnspring.webfluxtaskmanagerapp.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token=extractToken(exchange);
        if (token==null){
            return chain.filter(exchange);
        }
        return validateToken(token).flatMap(isValid->isValid?authenticateAndContinue(token,exchange,chain):handleInvalidtoken(exchange));
    }

    private Mono<Void> authenticateAndContinue(String token, ServerWebExchange exchange, WebFilterChain chain) {
        String username = jwtService.extractTokenSubject(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();

                    Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                });
    }



    private Mono<Void> handleInvalidtoken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    private String extractToken(ServerWebExchange exchange) {
        String token= exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token!=null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return null;
    }

    private Mono<Boolean> validateToken(String token) {
        return jwtService.validateJwt(token);
    }


}
