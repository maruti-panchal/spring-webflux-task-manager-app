package com.learnspring.webfluxtaskmanagerapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "token")
public class SecretConfig {
    private String secret;
}
