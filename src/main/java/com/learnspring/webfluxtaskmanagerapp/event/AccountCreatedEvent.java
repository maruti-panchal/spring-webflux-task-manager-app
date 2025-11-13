package com.learnspring.webfluxtaskmanagerapp.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class AccountCreatedEvent extends ApplicationEvent {
    private String userName;
    private String userEmail;
    public AccountCreatedEvent(Object source,String email,String username) {
        super(source);
        this.userEmail = email;
        this.userName = username;
    }
}
