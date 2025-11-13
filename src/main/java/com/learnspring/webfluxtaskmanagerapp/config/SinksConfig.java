package com.learnspring.webfluxtaskmanagerapp.config;


import com.learnspring.webfluxtaskmanagerapp.dtos.SignupResponseDto;
import com.learnspring.webfluxtaskmanagerapp.entity.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinksConfig {
    @Bean
    public Sinks.Many<SignupResponseDto> userStream() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
