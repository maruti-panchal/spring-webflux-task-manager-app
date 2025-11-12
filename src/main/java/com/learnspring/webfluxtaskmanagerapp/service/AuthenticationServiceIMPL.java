package com.learnspring.webfluxtaskmanagerapp.service;



import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceIMPL implements AuthenticationService {
    private final ReactiveAuthenticationManager reactiveAuthenticationManagerauthenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationServiceIMPL(ReactiveAuthenticationManager reactiveAuthenticationManagerauthenticationManager,UserRepository userRepository, JwtService jwtService) {
        this.reactiveAuthenticationManagerauthenticationManager = reactiveAuthenticationManagerauthenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Map<String, String>> authenticate(String username, String password) {
        return reactiveAuthenticationManagerauthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .then(getUserDetails(username))
                .map(this::createAuthenticationToken);

    }

    private Mono<UserEntity> getUserDetails(String username) {
        return userRepository.findByUsername(username);
    }

    private Map<String,String> createAuthenticationToken(UserEntity user) {
        Map<String,String> tokens = new HashMap<>();
        tokens.put("UserID",user.getId().toString());
        tokens.put("Token",jwtService.generateJwt(user.getUsername()));
        return tokens;
    }
}
