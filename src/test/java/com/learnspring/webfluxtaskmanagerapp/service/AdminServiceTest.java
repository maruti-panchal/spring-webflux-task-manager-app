package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.TaskEntity;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.TaskRepository;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;


    @InjectMocks
    private AdminService adminService;


    private final TaskEntity taskEntity1 = TaskEntity.builder()
            .id("t1").title("Task 1").description("Desc 1").dueDays(7).username("admin").build();
    private final TaskEntity taskEntity2 = TaskEntity.builder()
            .id("t2").title("Task 2").description("Desc 2").dueDays(3).username("user1").build();
    private final UserEntity userEntity = UserEntity.builder()
            .id("u1").username("user-to-delete").email("a@b.com").build();


    private TaskResponseDto taskResponse1;
    private TaskResponseDto taskResponse2;


    private Mono<SecurityContext> mockSecurityContextMono;

    @BeforeEach
    void setup() {

        taskResponse1 = TaskResponseDto.builder()
                .id(taskEntity1.getId())
                .title(taskEntity1.getTitle())
                .description(taskEntity1.getDescription())
                .dueDays(taskEntity1.getDueDays())
                .username(taskEntity1.getUsername())
                .build();
        taskResponse2 = TaskResponseDto.builder()
                .id(taskEntity2.getId())
                .title(taskEntity2.getTitle())
                .description(taskEntity2.getDescription())
                .dueDays(taskEntity2.getDueDays())
                .username(taskEntity2.getUsername())
                .build();


        Authentication mockAuthentication = mock(Authentication.class);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);

        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

        mockSecurityContextMono = Mono.just(mockSecurityContext);
    }


    private <T> Mono<T> withMockSecurity(Mono<T> mono) {
        return mono.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(mockSecurityContextMono));
    }

    private <T> Flux<T> withMockSecurity(Flux<T> flux) {
        return flux.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(mockSecurityContextMono));
    }


    @Test
    void testGetAllTasksReturnsAllTasksToAdmin() {
        when(taskRepository.findAll()).thenReturn(Flux.just(taskEntity1, taskEntity2));

        Flux<TaskResponseDto> resultFlux = withMockSecurity(adminService.getAllTasks());

        StepVerifier.create(resultFlux)
                .expectNextMatches(actualTask -> {
                    assertEquals(taskResponse1.getId(), actualTask.getId(), "Task ID must match task 1.");
                    assertEquals(taskResponse1.getTitle(), actualTask.getTitle(), "Task Title must match task 1.");
                    return true;
                })
                .expectNextMatches(actualTask -> {
                    assertEquals(taskResponse2.getId(), actualTask.getId(), "Task ID must match task 2.");
                    assertEquals(taskResponse2.getTitle(), actualTask.getTitle(), "Task Title must match task 2.");
                    return true;
                })
                .verifyComplete();

        verify(taskRepository, times(1)).findAll();
    }



    @Test
    void getTaskById_Found_ReturnsTask() {
        final String taskId = "t1";

        when(taskRepository.findById(taskId)).thenReturn(Mono.just(taskEntity1));

        Mono<TaskResponseDto> resultMono = withMockSecurity(adminService.getTaskById(taskId));

        StepVerifier.create(resultMono)
                .expectNextMatches(actualTask -> {
                    assertEquals(taskResponse1.getId(), actualTask.getId(), "Task ID must match the expected ID.");
                    assertEquals(taskResponse1.getTitle(), actualTask.getTitle(), "Task Title must match the expected Title.");

                    return true;
                })
                .verifyComplete();
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testGetTaskByIdNotFoundReturnsEmptyMonoToAdmin() {

        final String taskId = "missing";
        when(taskRepository.findById(taskId)).thenReturn(Mono.empty());

        Mono<TaskResponseDto> resultMono = withMockSecurity(adminService.getTaskById(taskId));

        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testDeleteTaskByIdSuccessReturnCompletes() {

        final String taskId = "t1";
        when(taskRepository.deleteById(taskId)).thenReturn(Mono.empty());

        Mono<Void> resultMono = withMockSecurity(adminService.deleteTaskById(taskId));

        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testDeleteAllTaskSuccessReturnCompletes() {
        when(taskRepository.deleteAll()).thenReturn(Mono.empty());

        Mono<Void> resultMono = withMockSecurity(adminService.deleteAllTask());

        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(taskRepository, times(1)).deleteAll();
    }

    @Test
    void testDeleteUserByIdUserExistsDeletesAndCompletes() {
        final String userId = "u1";
        when(userRepository.findById(userId)).thenReturn(Mono.just(userEntity));
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        Mono<Void> resultMono = withMockSecurity(adminService.deleteUserById(userId));

        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserByIdUserNotFoundCompletesWithoutDeletion() {

        final String userId = "missing-user";
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        Mono<Void> resultMono = withMockSecurity(adminService.deleteUserById(userId));


        StepVerifier.create(resultMono)
                .verifyComplete();


        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).deleteById(anyString());
    }
}