package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.dtos.LoginRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.LoginResponseDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignUpRequestDto;
import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthService authService;

    private final String ENCODED_PASSWORD = "encoded_password_hash";
    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_ROLE = "USER";
    private final String TEST_TOKEN = "mock.jwt.token";

    @Test
    void testSignupWithValidDetailsAndReturnUser() {

        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .firstName("Test")
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password("plaintext_password")
                .role(TEST_ROLE)
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id("new-id-123")
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(List.of(TEST_ROLE))
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedUser));

        Mono<SignupResponseDto> resultMono = authService.signup(Mono.just(requestDto));

        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    assertEquals("new-id-123", response.getId());
                    assertEquals(TEST_USERNAME, response.getUsername());
                    assertEquals(TEST_EMAIL, response.getEmail());
                    assertTrue(response.getRole().contains(TEST_ROLE));
                })
                .verifyComplete();

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        UserEntity capturedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, capturedUser.getPassword());
        assertEquals(TEST_USERNAME, capturedUser.getUsername());
        assertEquals(List.of(TEST_ROLE), capturedUser.getRoles());
    }

    @Test
    void testSignUpFailsAndReturnError() {

        SignUpRequestDto requestDto = SignUpRequestDto.builder().username(TEST_USERNAME).password("pass").role(TEST_ROLE).build();

        RuntimeException dbError = new RuntimeException("DB Error");

        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.error(dbError));

        Mono<SignupResponseDto> resultMono = authService.signup(Mono.just(requestDto));

        StepVerifier.create(resultMono)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testLoginWithValidDetailsAndReturnUser() {

        LoginRequestDto requestDto = LoginRequestDto.builder()
                .username(TEST_USERNAME)
                .password("plaintext_password")
                .build();

        Map<String, String> authMap = Map.of("Token", TEST_TOKEN, "UserID", "user-id-123");

        when(authenticationService.authenticate(TEST_USERNAME, "plaintext_password"))
                .thenReturn(Mono.just(authMap));

        Mono<LoginResponseDto> resultMono = authService.login(Mono.just(requestDto));

        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    assertEquals(TEST_USERNAME, response.getUsername());
                    assertEquals(TEST_TOKEN, response.getToken());
                })
                .verifyComplete();

        verify(authenticationService, times(1)).authenticate(TEST_USERNAME, "plaintext_password");
    }

    @Test
    void testLoginFailsAndReturnError() {

        LoginRequestDto requestDto = LoginRequestDto.builder()
                .username(TEST_USERNAME)
                .password("wrong_password")
                .build();

        AuthenticationException authException = new AuthenticationException("Bad Credentials") {};

        when(authenticationService.authenticate(TEST_USERNAME, "wrong_password"))
                .thenReturn(Mono.error(authException));

        Mono<LoginResponseDto> resultMono = authService.login(Mono.just(requestDto));

        StepVerifier.create(resultMono)
                .expectError(AuthenticationException.class)
                .verify();

        verify(authenticationService, times(1)).authenticate(TEST_USERNAME, "wrong_password");
    }
}