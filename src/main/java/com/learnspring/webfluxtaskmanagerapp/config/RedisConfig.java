package com.learnspring.webfluxtaskmanagerapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    private <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            Class<T> clazz) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> valuSeriakizer=new Jackson2JsonRedisSerializer<>(objectMapper,clazz);

        RedisSerializationContext<String,T> context=RedisSerializationContext.<String,T>newSerializationContext(keySerializer)
                .value(valuSeriakizer)
                .hashKey(keySerializer)
                .hashValue(valuSeriakizer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String,FakeStoreDto> fakeStoreReactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,ObjectMapper objectMapper){
        return reactiveRedisTemplate(factory,objectMapper,FakeStoreDto.class);
    }


}
