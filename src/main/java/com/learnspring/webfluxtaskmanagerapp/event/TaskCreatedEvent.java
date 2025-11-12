package com.learnspring.webfluxtaskmanagerapp.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class TaskCreatedEvent extends ApplicationEvent {
    private String username;
    private String title;
    public TaskCreatedEvent(Object source,String username,String title) {
        super(source);
        this.username = username;
        this.title = title;
    }
}
