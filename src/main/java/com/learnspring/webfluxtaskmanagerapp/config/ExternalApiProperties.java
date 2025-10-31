package com.learnspring.webfluxtaskmanagerapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix ="external.fakestore")
public class ExternalApiProperties {

    private String baseUrl;

}
