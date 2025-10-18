package com.learnspring.webfluxtaskmanagerapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskRequestDto {
    @NotBlank(message = "Username is Required")
    private String username;
    @NotBlank(message = "Title is Required")
    private String title;
    @NotBlank(message = "Description is Required")
    private String description;
    @NotBlank(message = "Due days is Required")
    private int dueDays;
}
