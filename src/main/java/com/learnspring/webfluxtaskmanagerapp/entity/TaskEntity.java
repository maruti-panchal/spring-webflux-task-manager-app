package com.learnspring.webfluxtaskmanagerapp.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    @Id
    private String id;
    private String username;
    private String title;
    private String description;
    private int dueDays;
}
