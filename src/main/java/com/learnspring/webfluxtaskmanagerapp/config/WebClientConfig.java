package com.learnspring.webfluxtaskmanagerapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${external.fakestore.base-url}")
    private String fakeStoreBaseUrl;


    @Bean
    public WebClient fakeStoreWebClient() {
        return WebClient.builder()
                .baseUrl(fakeStoreBaseUrl)
                .build();
    }


}
