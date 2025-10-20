package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
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


}
