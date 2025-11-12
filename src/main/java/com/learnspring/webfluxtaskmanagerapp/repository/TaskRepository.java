package com.learnspring.webfluxtaskmanagerapp.repository;

import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<TaskEntity, String> {
    Mono<TaskEntity> findById(String id);
    Flux<TaskEntity> findByUsername(String username);
    Mono<TaskEntity> findByIdAndUsername(String id, String username);
}
