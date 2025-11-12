package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.LoginRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.LoginResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignUpRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;

import com.learnspring.webfluxtaskmanagerapp.service.AuthService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<SignupResponseDto>> signup(@RequestBody Mono<SignUpRequestDto> signUpRequestDto){
        return authService.signup(signUpRequestDto).map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponseDto>> login(@RequestBody Mono<LoginRequestDto> loginRequestDto) {
        return authService.login(loginRequestDto).map(ResponseEntity::ok);
    }

}
