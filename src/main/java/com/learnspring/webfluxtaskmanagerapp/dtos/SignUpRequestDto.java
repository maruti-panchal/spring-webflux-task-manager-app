package com.learnspring.webfluxtaskmanagerapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@Builder
public class SignUpRequestDto {
    @Indexed(unique = true)
    @NotBlank(message = "Username is Required")
    private String username;
    @NotBlank(message = "firstname is Required")
    private String firstName;
    @NotBlank(message = "Lastname is Required")
    private String lastName;
    @NotBlank(message = "Email is Required")
    @Email(message = "Enter valid email id")
    private String email;
    @NotBlank(message = "Phone number is Required")
    private String phone;
    @NotBlank(message = "Password is Required")
    @Size(min = 5,max = 20,message = "Password must be greater than 5 characters")
    private String password;
    @NotBlank(message = "Role is Required")
    private String role;
}
