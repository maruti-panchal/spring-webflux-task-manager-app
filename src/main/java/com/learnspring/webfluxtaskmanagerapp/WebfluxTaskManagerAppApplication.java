package com.learnspring.webfluxtaskmanagerapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebfluxTaskManagerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxTaskManagerAppApplication.class, args);
    }

}
