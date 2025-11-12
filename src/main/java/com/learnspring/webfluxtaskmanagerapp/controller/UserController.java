package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.TaskRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.TaskResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.MyUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;




@RestController
@RequestMapping("/user")
public class UserController {

    private final MyUserService myUserService;

    public UserController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @GetMapping("/task")
    public Flux<TaskResponseDto> getTasks() {
        return myUserService.getMyTasks();
    }

    @PostMapping("/task")
    public Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestBody Mono<TaskRequestDto> taskDtoMono) {
        return myUserService.createTask(taskDtoMono)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.internalServerError().build()));
    }

    @GetMapping("/task/{id}")
    public Mono<ResponseEntity<TaskResponseDto>> getTask(@PathVariable String id) {
        return myUserService.getTaskById(id)
                .map(task -> ResponseEntity.status(HttpStatus.FOUND).body(task))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


    @DeleteMapping("/task/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return myUserService.deleteTaskById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("/task/{id}")
    public Mono<ResponseEntity<Void>> updateTask(@PathVariable String id,
                                                 @RequestBody TaskRequestDto taskRequestDto) {
        return myUserService.updateTaskById(id, taskRequestDto)
                .map(task -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
