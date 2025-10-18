package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public AdminController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Get all tasks (only if logged in as admin)
    @GetMapping("/task")
    public Flux<TaskResponseDto> getAllTask() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMapMany(auth -> taskRepository.findAll())
                .map(this::toResponse);
    }

    // Get task by ID
    @GetMapping("/task/{id}")
    public Mono<ResponseEntity<TaskResponseDto>> getTaskById(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> taskRepository.findById(id))
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete task by ID
    @DeleteMapping("/task/{id}")
    public Mono<ResponseEntity<Boolean>> deleteTaskById(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> taskRepository.deleteById(id).then(Mono.just(ResponseEntity.ok(true))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete all tasks
    @DeleteMapping("/task")
    public Mono<ResponseEntity<Boolean>> deleteAllTask() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> taskRepository.deleteAll().then(Mono.just(ResponseEntity.ok(true))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Delete user by ID
    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Boolean>> deleteUserById(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> userRepository.findById(id)
                        .flatMap(user -> userRepository.deleteById(id).then(Mono.just(ResponseEntity.ok(true))))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private TaskResponseDto toResponse(TaskEntity task) {
        return TaskResponseDto.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDays(task.getDueDays())
                .username(task.getUsername())
                .build();
    }
}
