package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

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
    void testGetAllTasksShouldReturnsAllTasksToAdmin() {
        when(adminService.getAllTasks()).thenReturn(Flux.just(task1, task2));
        Flux<TaskResponseDto> tasksFlux=adminController.getAllTask();
        StepVerifier
                .create(tasksFlux)
                .expectNext(task1, task2)
                .verifyComplete();
        verify(adminService,Mockito.times(1)).getAllTasks();
    }

    @Test
    void testGetTaskByIdShouldReturnsTaskToAdmin() {
        final String id="t1";
        when(adminService.getTaskById(id)).thenReturn(Mono.just(task1));
        Mono<ResponseEntity<TaskResponseDto>> monoTask=adminController.getTaskById(id);
        StepVerifier.create(monoTask).expectNextMatches(taskResponseDtoResponseEntity -> {
            assertEquals(HttpStatus.FOUND, taskResponseDtoResponseEntity.getStatusCode());
            assertEquals(id, taskResponseDtoResponseEntity.getBody().getId());
            return true;
        })
                .verifyComplete();
        verify(adminService,Mockito.times(1)).getTaskById(id);
    }

    @Test
    void testDeleteTaskByIdShouldReturnTrueToAdmin() {
        final String id="t1";
        when(adminService.deleteTaskById(id)).thenReturn(Mono.empty());
        Mono<ResponseEntity<Boolean>> isDeleted=adminController.deleteTaskById(id);
        StepVerifier.create(isDeleted).expectNextMatches(response->{
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertEquals(true, response.getBody());
            return true;
        }).verifyComplete();
        verify(adminService,Mockito.times(1)).deleteTaskById(id);
    }

    @Test
    void testDeleteAllTaskShouldReturnTrueToAdmin() {
        when(adminService.deleteAllTask()).thenReturn(Mono.empty());
        Mono<ResponseEntity<Boolean>> isDeleted=adminController.deleteAllTask();
        StepVerifier.create(isDeleted)
                .expectNextMatches(resonse->{
                    assertEquals(HttpStatus.NO_CONTENT,resonse.getStatusCode());
                    assertEquals(true, resonse.getBody());
                    return true;
                }).verifyComplete();
        verify(adminService,Mockito.times(1)).deleteAllTask();
    }

    @Test
    void testDeleteUserByIdShouldReturnTrueToAdmin() {
        final String id="t1";
        when(adminService.deleteUserById(id)).thenReturn(Mono.empty());
        Mono<ResponseEntity<Boolean>> isDeleted=adminController.deleteUserById(id);
        StepVerifier.create(isDeleted).expectNextMatches(response->{
            assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
            assertEquals(true, response.getBody());
            return true;
        }).verifyComplete();
        verify(adminService,Mockito.times(1)).deleteUserById(id);
    }


}