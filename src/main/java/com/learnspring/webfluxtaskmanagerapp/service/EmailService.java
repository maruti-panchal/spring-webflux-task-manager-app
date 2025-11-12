package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.event.TaskCreatedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService{

    @Async
    @EventListener
    public void sendEmail(TaskCreatedEvent event){
        System.out.println("Sending Email to "+event.getTitle()+" Task created");
    }

}
