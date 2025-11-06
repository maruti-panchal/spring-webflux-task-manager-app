package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.config.ReactorMdc;
import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import com.learnspring.webfluxtaskmanagerapp.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
@Slf4j
public class WebClientController {

    private final WebClientService webClientService;

    @GetMapping("/products")
    public Flux<FakeStoreDto> getProducts(@RequestHeader(name = "Authorization") String token) {
        return webClientService.getProducts(token)
                .transform(ReactorMdc.mdcLifterFlux())
                .doOnSubscribe(s -> log.debug("Fetching product list..."))
                .doOnNext(p -> log.info("Fetched product"))
                .doOnComplete(() -> log.info("Completed fetching all products"))
                .doOnError(err -> log.error("Error while fetching products: {}", err.getMessage()));
    }


    @GetMapping("/products/{id}")
    public Mono<ResponseEntity<FakeStoreDto>> getProductById(@PathVariable("id") String id,@RequestHeader(name = "Authorization") String token) {
        return webClientService.getProductById(id,token)
                .transform(ReactorMdc.mdcLifterMono()) // ensures MDC for this reactive chain
                .doOnSubscribe(s -> log.debug("Fetching product with id={}", id))
                .doOnSuccess(p -> log.info("Fetched product successfully id={}", id))
                .doOnError(err -> log.error("Failed to fetch product id={}, reason={}", id, err.getMessage()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public Mono<ResponseEntity<FakeStoreDto>> createProduct(@RequestBody FakeStoreDto product,@RequestHeader(name = "Authorization") String token) {
        return webClientService.createProduct(product,token)
                .transform(ReactorMdc.mdcLifterMono())
                .doOnSubscribe(s -> log.debug("Creating product: {}", product.title))
                .doOnSuccess(saved -> log.info("Created product successfully: id={} name={}", saved.id, saved.title))
                .doOnError(err -> log.error("Error creating product: {}", err.getMessage()))
                .map(ResponseEntity::ok);
    }

}
