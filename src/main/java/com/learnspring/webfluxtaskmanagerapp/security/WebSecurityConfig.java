package com.learnspring.webfluxtaskmanagerapp.security;


//import com.learnspring.webfluxtaskmanagerapp.filters.RequestCostFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class WebSecurityConfig{
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final RequestCostFilter requestCostFilter;

    public WebSecurityConfig(ReactiveAuthenticationManager reactiveAuthenticationManager, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.requestCostFilter = requestCostFilter;
    }
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authorizeExchange((exchange) -> {
                    exchange.pathMatchers("/auth/**").permitAll();
                    exchange.pathMatchers("/web/**").permitAll();
                    exchange.pathMatchers("/index.html").permitAll();
                    exchange.pathMatchers("/admin/stream").permitAll();
                    exchange.pathMatchers("/user/**").hasRole("USER");
                    exchange.pathMatchers("/admin/**").hasRole("ADMIN");
                    exchange.anyExchange().authenticated();
                })
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .addFilterAt(requestCostFilter, SecurityWebFiltersOrder.FIRST)
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
