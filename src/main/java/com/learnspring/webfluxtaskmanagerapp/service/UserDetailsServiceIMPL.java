package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.repository.UserRepository;


import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceIMPL implements ReactiveUserDetailsService {
    private final UserRepository userRepository;
    public UserDetailsServiceIMPL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .map(userEntity ->
                        User.builder()
                                .username(userEntity.getUsername())
                                .password(userEntity.getPassword())
                                .roles(userEntity.getRoles().toArray(new String[0]))
                                .build()
                );
    }


}
