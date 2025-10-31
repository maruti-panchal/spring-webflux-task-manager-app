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
    public Flux<FakeStoreDto> getProducts() {
        return webClientService.getProducts();
    }

    @GetMapping("/products/{id}")
    public Mono<ResponseEntity<FakeStoreDto>> getProductById(@PathVariable("id") String id) {
        return webClientService.getProductById(id).map(ResponseEntity::ok);
    }

    @PostMapping("/products")
    public Mono<ResponseEntity<FakeStoreDto>> createProduct(@RequestBody FakeStoreDto product) {
        return webClientService.createProduct(product).map(ResponseEntity::ok);
    }

}
