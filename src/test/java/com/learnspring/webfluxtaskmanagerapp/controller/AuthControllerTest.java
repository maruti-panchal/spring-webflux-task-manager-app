package com.learnspring.webfluxtaskmanagerapp.controller;

import com.learnspring.webfluxtaskmanagerapp.dtos.LoginRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.LoginResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignUpRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure Unit Tests for AuthController.
 * Bypasses the HTTP layer and uses StepVerifier to assert on reactive Mono streams.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;


    @Test
    void testSignupWithValidUserDetailsReturnsCreatedUser() {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .email("maruti@gmail.com")
                .username("maruti1")
                .password("Mpanchat@123")
                .role("USER")
                .build();
        SignupResponseDto serviceResponse = SignupResponseDto.builder()
                .id("1")
                .username("maruti1")
                .email("maruti@gmail.com")
                .role("[USER]")
                .build();
        when(authService.signup(any(Mono.class))).thenReturn(Mono.just(serviceResponse));
        Mono<ResponseEntity<SignupResponseDto>> responseEntityMono =
                authController.signup(Mono.just(requestDto));
        StepVerifier.create(responseEntityMono)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "HTTP Status must be 200 OK");
                    assertEquals("maruti1", responseEntity.getBody().getUsername(), "Username must match");
                    assertEquals("[USER]", responseEntity.getBody().getRole(), "Role must match");
                    return true;
                })
                .verifyComplete();
        verify(authService, times(1)).signup(any(Mono.class));
    }

    @Test
    void testSignupWithInvalidUserDetailsThrowsException() {
        SignUpRequestDto requestDto = SignUpRequestDto.builder().username("existinguser").password("pass").build();
        when(authService.signup(any(Mono.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")));
        Mono<ResponseEntity<SignupResponseDto>> responseEntityMono =
                authController.signup(Mono.just(requestDto));
        StepVerifier.create(responseEntityMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.CONFLICT
                )
                .verify();
    }

    @Test
    void login_Success_Returns200OK() {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .username("maruti1").password("Mpanchat@123").build();
        LoginResponseDto serviceResponse = LoginResponseDto.builder()
                .username("maruti1").token("mock-jwt-token-xyz").build();
        when(authService.login(any(Mono.class))).thenReturn(Mono.just(serviceResponse));
        Mono<ResponseEntity<LoginResponseDto>> responseEntityMono =
                authController.login(Mono.just(requestDto));
        StepVerifier.create(responseEntityMono)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "HTTP Status must be 200 OK");
                    assertEquals("maruti1", responseEntity.getBody().getUsername(), "Username in body must match");
                    assertNotNull(responseEntity.getBody().getToken(), "Token must be present");
                    return true;
                })
                .verifyComplete();
        verify(authService, times(1)).login(any(Mono.class));
    }

    @Test
    void login_Unauthorized_VerifiesError() {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .username("maruti1").password("wrongpassword").build();
        when(authService.login(any(Mono.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials")));
        Mono<ResponseEntity<LoginResponseDto>> responseEntityMono =
                authController.login(Mono.just(requestDto));
        StepVerifier.create(responseEntityMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.UNAUTHORIZED
                )
                .verify();
    }
}