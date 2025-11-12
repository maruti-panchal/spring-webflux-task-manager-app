//package com.learnspring.webfluxtaskmanagerapp.service;
//
//import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
//import com.learnspring.webfluxtaskmanagerapp.repository.Productrepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//public class ProductElasticService {
//    private final Productrepository productrepository;
//    public Flux<FakeStoreDto> getProducts(){
//        return productrepository.findAll();
//    }
//
//    public Mono<FakeStoreDto> getElasticProductById(String id){
//        return productrepository.findById(id);
//    }
//
//    public Mono<FakeStoreDto> createProductForElastic(FakeStoreDto product) {
//        return productrepository.save(product);
//    }
//
//}
