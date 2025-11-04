package com.learnspring.webfluxtaskmanagerapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class WebClientService {
    private final WebClient webClient;
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private final ReactiveRedisTemplate<String, FakeStoreDto> redisTemplate;



    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String PRODUCT_KEY = "product:";
    private static final String PRODUCTS_KEY = "products:all";



    public Flux<FakeStoreDto> getProducts(String token) {

        return redisTemplate.opsForList().range(PRODUCTS_KEY, 0, -1)
                .switchIfEmpty(
                        webClient.get()
                                .uri("/products")
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToFlux(FakeStoreDto.class)
                                .collectList()
                                .flatMapMany(products ->
                                        redisTemplate.opsForList()
                                                .rightPushAll(PRODUCTS_KEY, products)
                                                .then(redisTemplate.expire(PRODUCTS_KEY, CACHE_TTL))
                                                .thenMany(Flux.fromIterable(products))
                                )
                );
    }


    public Mono<FakeStoreDto> getProductById(String id, String token) {
        return redisTemplate.opsForValue().get(PRODUCT_KEY)
                .switchIfEmpty(
                        webClient.get()
                                .uri("/products/{id}", id)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .bodyToMono(FakeStoreDto.class)
                                .flatMap(product ->
                                        redisTemplate.opsForValue()
                                                .set(PRODUCT_KEY, product, CACHE_TTL) // store in Redis with TTL
                                                .thenReturn(product)
                                )
                );
    }

    public Mono<FakeStoreDto> createProduct(FakeStoreDto request, String token) {
        return webClient.post()
                .uri("/products")
                .header("Authorization", "Bearer "+token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(new RuntimeException("Client error while creating product")))
                .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new RuntimeException("Server error while creating product")))
                .bodyToMono(FakeStoreDto.class);
    }
}
