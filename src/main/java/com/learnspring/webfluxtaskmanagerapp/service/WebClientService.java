package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service

public class WebClientService {
    private WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }
    public Flux<FakeStoreDto> getProducts() {
        return webClient
                .get()
                .uri("https://fakestoreapi.com/products")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,res-> Mono.error(new UsernameNotFoundException("Username not found")))
                .onStatus(HttpStatusCode::is5xxServerError,res-> Mono.error(new InternalError("Internal Server Error")))
                .bodyToFlux(FakeStoreDto.class);
    }

    public Mono<FakeStoreDto> getProductById(String id) {
        return webClient
                .get()
                .uri("https://fakestoreapi.com/products/"+id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,res-> Mono.error(new UsernameNotFoundException("Username not found")))
                .onStatus(HttpStatusCode::is5xxServerError,res-> Mono.error(new InternalError("Internal Server Error")))
                .bodyToMono(FakeStoreDto.class);
    }

    public Mono<FakeStoreDto> createProduct(FakeStoreDto request) {
        return webClient.post()
                .uri("https://fakestoreapi.com/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(new RuntimeException("Client error while creating product")))
                .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new RuntimeException("Server error while creating product")))
                .bodyToMono(FakeStoreDto.class);
    }
}
