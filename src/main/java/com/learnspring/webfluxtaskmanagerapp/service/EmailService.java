package com.learnspring.webfluxtaskmanagerapp.service;

import com.learnspring.webfluxtaskmanagerapp.event.AccountCreatedEvent;
import com.learnspring.webfluxtaskmanagerapp.event.TaskCreatedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService{
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    @EventListener
    public void sendEmail(AccountCreatedEvent event){
       try{
           SimpleMailMessage mailMessage = new SimpleMailMessage();
           mailMessage.setTo(event.getUserEmail());
           mailMessage.setSubject("🎉 Welcome to TaskManager, " + event.getUserName() + "!");

           String message = """
                Hi %s 👋,

                Welcome to *TaskManager*! 🎯
                Your account has been created successfully ✅

                You can now start managing your daily tasks, setting goals, and tracking progress effortlessly 🚀

                👉 Login here: http://127.0.0.1:8080/auth/login

                If you didn’t create this account, please ignore this email or contact our support team.

                Cheers,
                The TaskManager Team 💼
                """.formatted(event.getUserName());

           mailMessage.setText(message);
           mailSender.send(mailMessage);

           System.out.println("✅ Welcome email sent to " + event.getUserEmail());
       }catch (Exception ex){
           System.out.println(ex.getMessage());
       }
    }

}
