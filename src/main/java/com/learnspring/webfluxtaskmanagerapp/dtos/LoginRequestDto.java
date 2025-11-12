package com.learnspring.webfluxtaskmanagerapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequestDto {
    @NotBlank(message = "Username is Required")
    private String username;
    @NotBlank(message = "Password is Required")
    private String password;
}
