package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

    private final TaskRepository taskRepository;

    public UserController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ✅ Get all tasks for logged-in user
    @GetMapping("/task")
    public Flux<TaskResponseDto> getTasks() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMapMany(taskRepository::findByUsername)
                .map(this::toResponse);
    }

    // ✅ Create new task for logged-in user
    @PostMapping("/task")
    public Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestBody Mono<TaskRequestDto> taskDtoMono) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .zipWith(taskDtoMono)
                .map(tuple -> {
                    String username = tuple.getT1();
                    TaskRequestDto dto = tuple.getT2();
                    TaskEntity entity = new TaskEntity(null, dto.getTitle(), dto.getDescription(), username,dto.getDueDays());
                    return entity;
                })
                .flatMap(taskRepository::save)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // ✅ Get task by id (only if it belongs to the user)
    @GetMapping("/task/{id}")
    public Mono<ResponseEntity<TaskResponseDto>> getTask(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findByIdAndUsername(id, username))
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // ✅ Delete task
    @DeleteMapping("/task/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findByIdAndUsername(id, username))
                .flatMap(task -> taskRepository.delete(task)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // --- Helper ---
    private TaskResponseDto toResponse(TaskEntity entity) {
        return TaskResponseDto.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .dueDays(entity.getDueDays())
                .username(entity.getUsername())
                .build();
    }
}
