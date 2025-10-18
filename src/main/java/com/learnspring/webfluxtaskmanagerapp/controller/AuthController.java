package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.LoginRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.LoginResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignUpRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import com.learnspring.webfluxtaskmanagerapp.service.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<SignupResponseDto>> signup(@RequestBody Mono<SignUpRequestDto> signUpRequestDto){
        return signUpRequestDto
                .map(this::convertSignUpRequestDtoToUserEntity)
                .flatMap(userRepository::save)
                .map(this::convertUserEntityToSignUpResponseDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponseDto>> login(@RequestBody Mono<LoginRequestDto> loginRequestDto) {
        return loginRequestDto.flatMap(loginRequest->
                authenticationService
                        .authenticate(loginRequest.getUsername(),loginRequest.getPassword())
                        .map(authResultMap->ResponseEntity.ok()
                                .header(HttpHeaders.AUTHORIZATION,"Bearer "+authResultMap.get("Token"))
                                .header("UserId",authResultMap.get("UserID"))
                                .body(LoginResponseDto.builder().username(loginRequest.getUsername()).token(authResultMap.get("Token")).build()))


        );
    }



    private UserEntity convertSignUpRequestDtoToUserEntity(SignUpRequestDto signUpRequestDto){
        return UserEntity.builder()
                .firstName(signUpRequestDto.getFirstName())
                .lastName(signUpRequestDto.getLastName())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .roles(List.of(signUpRequestDto.getRole()))
                .username(signUpRequestDto.getUsername())
                .phone(signUpRequestDto.getPhone())
                .build();
    }

    private SignupResponseDto convertUserEntityToSignUpResponseDto(UserEntity userEntity){
        return SignupResponseDto.builder()
                .id(userEntity.getId().toString())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .role(userEntity.getRoles().toString())
                .build();
    }
}
