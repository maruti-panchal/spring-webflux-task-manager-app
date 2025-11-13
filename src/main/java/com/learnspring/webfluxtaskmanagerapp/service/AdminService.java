package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class AdminService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final Sinks.Many<SignupResponseDto> userEntitySink;

    public AdminService(TaskRepository taskRepository, UserRepository userRepository, Sinks.Many<SignupResponseDto> userEntitySink) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userEntitySink = userEntitySink;
    }

    public Flux<TaskResponseDto> getAllTasks() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(auth -> taskRepository.findAll())
                .map(this::toResponse);
    }

    public Mono<TaskResponseDto> getTaskById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> taskRepository.findById(id))
                .map(this::toResponse);

    }

    public Mono<Void> deleteTaskById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> taskRepository.deleteById(id));

    }

    public Mono<Void> deleteAllTask() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> taskRepository.deleteAll());

    }

    public Mono<Void> deleteUserById(String id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> userRepository.findById(id)
                        .flatMap(user -> userRepository.deleteById(id)));

    }
    private TaskResponseDto toResponse(TaskEntity task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDays(task.getDueDays())
                .username(task.getUsername())
                .build();
    }


}
