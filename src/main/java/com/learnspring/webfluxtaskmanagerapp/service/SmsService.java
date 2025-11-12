package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.event.TaskCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SmsService {
    @Async
    @EventListener
    public void sendSms(TaskCreatedEvent event){
        System.out.println("Sending SMS to "+event.getTitle()+" Task created");
    }
}
