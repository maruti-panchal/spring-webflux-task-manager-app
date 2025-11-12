package com.learnspring.webfluxtaskmanagerapp.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupResponseDto {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String role;
}
