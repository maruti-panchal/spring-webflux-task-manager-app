package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public UserController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/task")
    public Flux<TaskResponseDto> getTasks() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMapMany(taskRepository::findByUsername)
                .map(this::toResponse);
    }

    @PostMapping("/task")
    public Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestBody Mono<TaskRequestDto> taskDtoMono) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())       // username string
                .zipWith(taskDtoMono)                                 // (username, dto)
                .flatMap(tuple -> {
                    String username = tuple.getT1();
                    TaskRequestDto dto = tuple.getT2();
                    // ensure TaskEntity has a field for creator (createdBy) or keep username separately
                    TaskEntity entity = new TaskEntity(null, dto.getTitle(), dto.getDescription(), username, dto.getDueDays());
                    // save task then forward both savedTask and username
                    return taskRepository.save(entity)
                            .map(savedTask -> Tuples.of(savedTask, username));
                })
                .flatMap(tuple -> {
                    TaskEntity savedTask = tuple.getT1();
                    String username = tuple.getT2();                    // use the original username variable
                    return userRepository.findByUsername(username)
                            .flatMap(user -> {
                                if (user.getTasks() == null) user.setTasks(new ArrayList<>());
                                user.getTasks().add(savedTask);
                                return userRepository.save(user).thenReturn(savedTask);
                            })
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
                })
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }




    @GetMapping("/task/{id}")
    public Mono<ResponseEntity<TaskResponseDto>> getTask(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findByIdAndUsername(id, username))
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


    @DeleteMapping("/task/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findByIdAndUsername(id, username))
                .flatMap(task -> taskRepository.delete(task)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


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
