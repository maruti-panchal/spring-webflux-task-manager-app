package com.learnspring.webfluxtaskmanagerapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@Component
@ConfigurationProperties(prefix ="external.fakestore")
public class ExternalApiProperties {

    private String baseUrl;

}
