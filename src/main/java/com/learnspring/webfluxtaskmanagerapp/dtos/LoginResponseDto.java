package com.learnspring.webfluxtaskmanagerapp.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    private String username;
    private String token;
}
