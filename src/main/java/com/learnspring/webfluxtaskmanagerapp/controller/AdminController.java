package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.service.AdminService;
import com.learnspring.webfluxtaskmanagerapp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(AdminService adminService, AuthService authService) {

        this.adminService = adminService;
        this.authService = authService;
    }


    @GetMapping("/task")
    public Flux<TaskResponseDto> getAllTask() {
        return adminService.getAllTasks();
    }


    @GetMapping("/task/{id}")
    public Mono<ResponseEntity<TaskResponseDto>> getTaskById(@PathVariable String id) {
        return adminService.getTaskById(id)
                .map(task->ResponseEntity.status(HttpStatus.FOUND).body(task));
    }


    @DeleteMapping("/task/{id}")
    public Mono<ResponseEntity<Boolean>> deleteTaskById(@PathVariable String id) {
        return adminService.deleteTaskById(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)));
    }

    // Delete all tasks
    @DeleteMapping("/task")
    public Mono<ResponseEntity<Boolean>> deleteAllTask() {
        return adminService.deleteAllTask()
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)));
    }

    // Delete user by ID
    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<Boolean>> deleteUserById(@PathVariable String id) {
        return adminService.deleteUserById(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)));
    }

    @GetMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SignupResponseDto> getAllTaskStream() {
        return authService.userStream();
    }


}
