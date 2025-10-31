package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import com.learnspring.webfluxtaskmanagerapp.service.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebClientController {

    private final WebClientService webClientService;

    @GetMapping("/products")
    public Flux<FakeStoreDto> getProducts(@RequestHeader(name = "Authorization") String token) {
        return webClientService.getProducts(token);
    }

    @GetMapping("/products/{id}")
    public Mono<ResponseEntity<FakeStoreDto>> getProductById(@PathVariable("id") String id,@RequestHeader(name = "Authorization") String token) {
        return webClientService.getProductById(id,token).map(ResponseEntity::ok);
    }

    @PostMapping("/products")
    public Mono<ResponseEntity<FakeStoreDto>> createProduct(@RequestBody FakeStoreDto product,@RequestHeader(name = "Authorization") String token) {
        return webClientService.createProduct(product,token).map(ResponseEntity::ok);
    }

}
