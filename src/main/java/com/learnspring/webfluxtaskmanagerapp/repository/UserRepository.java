package com.learnspring.webfluxtaskmanagerapp.repository;

import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
    Mono<UserEntity> findByUsername(String username);


}
