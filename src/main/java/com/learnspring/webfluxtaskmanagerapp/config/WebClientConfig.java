package com.learnspring.webfluxtaskmanagerapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

//    @Value("${external.fakestore.base-url}")
//    private String fakeStoreBaseUrl;


    private final ExternalApiProperties externalApiProperties;


    @Bean
    public WebClient fakeStoreWebClient() {
        return WebClient.builder()
                .baseUrl(externalApiProperties.getBaseUrl())
                .build();
    }


}
