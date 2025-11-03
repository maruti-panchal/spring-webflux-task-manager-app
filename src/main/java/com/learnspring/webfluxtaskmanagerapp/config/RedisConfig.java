package com.learnspring.webfluxtaskmanagerapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {



    @Bean
    public ReactiveRedisTemplate<String, FakeStoreDto> fakeStoreRedisTemplate(
            ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<FakeStoreDto> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, FakeStoreDto.class);

        RedisSerializationContext<String, FakeStoreDto> context =
                RedisSerializationContext.<String, FakeStoreDto>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .hashKey(keySerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
