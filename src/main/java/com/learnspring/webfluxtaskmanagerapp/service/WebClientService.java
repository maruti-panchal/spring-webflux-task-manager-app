package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, FakeStoreDto> redisTemplate;

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private static final String PRODUCT_KEY = "product:";
    private static final String PRODUCTS_KEY = "products:all";


    public Flux<FakeStoreDto> getProducts(String token) {
        log.info("Fetching all products");

        return redisTemplate.opsForList().range(PRODUCTS_KEY, 0, -1)
                .switchIfEmpty(fetchAndCacheProducts(token))
                .doOnSubscribe(sub -> log.debug("Started fetching products"))
                .doOnComplete(() -> log.debug("Completed fetching products"))
                .doOnError(e -> log.error("Error fetching products", e));
    }


    public Mono<FakeStoreDto> getProductById(String id, String token) {
        log.info("Fetching product with ID: {}", id);

        return redisTemplate.opsForValue().get(PRODUCT_KEY + id)
                .switchIfEmpty(fetchAndCacheProductById(id, token))
                .doOnSubscribe(sub -> log.debug("Started fetching product {}", id))
                .doOnSuccess(p -> log.debug("Fetched product: {}", p))
                .doOnError(e -> log.error("Error fetching product by ID {}", id, e));
    }


    public Mono<FakeStoreDto> createProduct(FakeStoreDto request, String token) {
        log.info("Creating product: {}", request);

        return webClient.post()
                .uri("/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        res -> Mono.error(new RuntimeException("Client error while creating product")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        res -> Mono.error(new RuntimeException("Server error while creating product")))
                .bodyToMono(FakeStoreDto.class)
                .doOnSuccess(product -> log.info("Product created successfully: {}", product))
                .doOnError(e -> log.error("Error creating product", e));
    }



    private Flux<FakeStoreDto> fetchAndCacheProducts(String token) {
        return webClient.get()
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
                .doOnNext(p -> log.debug("Cached product: {}", p.title))
                .doOnError(e -> log.error("Error caching product list", e));
    }

    private Mono<FakeStoreDto> fetchAndCacheProductById(String id, String token) {
        return webClient.get()
                .uri("/products/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(FakeStoreDto.class)
                .flatMap(product ->
                        redisTemplate.opsForValue()
                                .set(PRODUCT_KEY + id, product, CACHE_TTL)
                                .thenReturn(product)
                )
                .doOnSuccess(p -> log.debug("Cached product ID {} successfully", id))
                .doOnError(e -> log.error("Error caching product {}", id, e));
    }
}
