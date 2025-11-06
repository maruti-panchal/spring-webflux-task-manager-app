package com.learnspring.webfluxtaskmanagerapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

@Component
public class ReactorConfig {
    @PostConstruct
    public void enablePropagation() {
        // Enables Reactor automatic context propagation so Reactor Context travels across threads/operators
        Hooks.enableAutomaticContextPropagation();
    }
}