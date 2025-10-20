package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;

@Service
public class MyUserService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public MyUserService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Flux<TaskResponseDto> getMyTasks() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMapMany(taskRepository::findByUsername)
                .map(this::toResponse);
    }

    public Mono<TaskResponseDto> createTask(Mono<TaskRequestDto> taskDtoMono) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .zipWith(taskDtoMono)
                .flatMap(tuple -> {
                    String username = tuple.getT1();
                    TaskRequestDto dto = tuple.getT2();
                    TaskEntity entity = new TaskEntity(null, dto.getTitle(), dto.getDescription(), username, dto.getDueDays());
                    return taskRepository.save(entity)
                            .map(savedTask -> Tuples.of(savedTask, username));
                })
                .flatMap(tuple -> {
                    TaskEntity savedTask = tuple.getT1();
                    String username = tuple.getT2();                    // use the original username variable
                    return userRepository.findByUsername(username)
                            .flatMap(user -> {
                                if (user.getTasks() == null) user.setTasks(new ArrayList<>());
                                user.getTasks().add(savedTask.getId());
                                return userRepository.save(user).thenReturn(savedTask);
                            })
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
                })
                .map(this::toResponse);
    }

    public Mono<TaskResponseDto> getTaskById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findById(id))
                .map(this::toResponse);
    }

    public Mono<Void> deleteTaskById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> taskRepository.findByIdAndUsername(id, username))
                .flatMap(taskRepository::delete);
    }

    public Mono<TaskResponseDto> updateTaskById(String id, TaskRequestDto taskRequestDto) {
        return taskRepository.findById(id)
                .flatMap(existingTask -> {
                    existingTask.setTitle(taskRequestDto.getTitle());
                    existingTask.setDescription(taskRequestDto.getDescription());
                    existingTask.setDueDays(taskRequestDto.getDueDays());
                    return taskRepository.save(existingTask);
                })
                .map(this::toResponse);
    }

    private TaskResponseDto toResponse(TaskEntity entity) {
        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .dueDays(entity.getDueDays())
                .username(entity.getUsername())
                .build();
    }
}
