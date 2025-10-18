package com.learnspring.webfluxtaskmanagerapp.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Builder
@Getter
@Setter
public class UserEntity {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private List<String> roles;
    @DBRef
    private List<TaskEntity> tasks=new ArrayList<>();
}
