package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.MyUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private MyUserService myUserService;

    @InjectMocks
    private UserController userController;

    private final TaskResponseDto task1 = TaskResponseDto
            .builder()
            .id("t1")
            .title("Task 1")
            .description("Need to complete task")
            .dueDays(7)
            .username("maruti1")
            .build();
    private final TaskResponseDto task2 = TaskResponseDto
            .builder()
            .id("t2")
            .title("Task 2")
            .description("Need to complete task")
            .dueDays(7)
            .username("maruti1")
            .build();

    private final TaskRequestDto newTask = TaskRequestDto
            .builder()
            .title("New Task")
            .description("Desc")
            .dueDays(7)
            .username("maruti1")
            .build();


    @Test
    void testGetAllTasksShouldReturnsAllTasks() {
        when(myUserService.getMyTasks()).thenReturn(Flux.just(task1, task2));
        Flux<TaskResponseDto> taskFlux = userController.getTasks();
        StepVerifier.create(taskFlux)
                .expectNext(task1)
                .expectNext(task2)
                .verifyComplete();
        verify(myUserService, times(1)).getMyTasks();
    }

    @Test
    void testCreateTaskShouldCreateNewTask() {
        when(myUserService.createTask(any(Mono.class))).thenReturn(Mono.just(task1));
        Mono<ResponseEntity<TaskResponseDto>> responseMono = userController.createTask(Mono.just(newTask));
        StepVerifier.create(responseMono)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "HTTP Status must be 200 OK");
                    assertEquals("t1", Objects.requireNonNull(responseEntity.getBody()).getId());
                    return true;
                })
                .verifyComplete();
        verify(myUserService, times(1)).createTask(any(Mono.class));
    }

    @Test
    void testFindTaskByIdShouldReturnsTask() {
        final String taskId = "t1";
        when(myUserService.getTaskById(taskId)).thenReturn(Mono.just(task1));
        Mono<ResponseEntity<TaskResponseDto>> responseMono = userController.getTask(taskId);
        StepVerifier.create(responseMono)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode(), "HTTP Status must be 302 FOUND");
                    assertEquals(taskId, Objects.requireNonNull(responseEntity.getBody()).getId());
                    return true;
                })
                .verifyComplete();
        verify(myUserService, times(1)).getTaskById(taskId);
    }

    @Test
    void testCreateTaskFailedShouldReturnError() {
        when(myUserService.createTask(any(Mono.class)))
                .thenReturn(Mono.empty());
        Mono<ResponseEntity<TaskResponseDto>> responseMono = userController.createTask(Mono.just(newTask));
        StepVerifier.create(responseMono)
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
                )
                .verifyComplete();
        verify(myUserService, times(1)).createTask(any(Mono.class));
    }
}