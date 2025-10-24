package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.LoginRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.LoginResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignUpRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AuthController's signup method.
 * Does not load Spring Security or the full Spring context.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(authController).build();
    }

    @Test
    void signup_Success() {

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .phone("1234567890")
                .role("USER")
                .build();


        SignupResponseDto responseDto = SignupResponseDto.builder()
                .id("mock-user-id-123")
                .username("testuser")
                .email("test@example.com")
                .phone("1234567890")
                .role("[USER]")
                .build();


        when(authService.signup(any(Mono.class))).thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), SignUpRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SignupResponseDto.class)
                .value(response -> {
                    Objects.requireNonNull(response);
                    assert response.getUsername().equals("testuser");
                    assert response.getId().equals("mock-user-id-123");
                });
    }


    @Test
    void signup_UserAlreadyExists_ReturnsInternalServerError() {

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username("existinguser")
                .password("anypass")
                .build();

        when(authService.signup(any(Mono.class)))
                .thenReturn(Mono.error(new RuntimeException("Duplicate Key Error: Username already exists")));

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), SignUpRequestDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void login_Success(){

        LoginRequestDto requestDto = LoginRequestDto
                .builder()
                .username("maruti1")
                .password("password123")
                .build();
        LoginResponseDto responseDto = LoginResponseDto
                .builder()
                .username("maruti1")
                .token("ahjhjhafjc-acuna")
                .build();
        when(authService.login(any(Mono.class))).thenReturn(Mono.just(responseDto));
        webTestClient
                .post()
                .uri("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), LoginRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDto.class)
                .value(response -> {
                    Objects.requireNonNull(response);
                    assert response.getUsername().equals("maruti1");
                    assert response.getToken().equals("ahjhjhafjc-acuna");
                });
    }

    @Test void login_UserNotFound_ReturnsInternalServerError() {
        LoginRequestDto requestDto = LoginRequestDto
                .builder()
                .username("maruti1")
                .password("password123")
                .build();
        when(authService.login(any(Mono.class))).thenReturn(Mono.error(new RuntimeException("User not found")));
        webTestClient.post()
                .uri("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), LoginRequestDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}