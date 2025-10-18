package com.learnspring.webfluxtaskmanagerapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class TaskEntity {
    @Id
    private ObjectId id;
    private String username;
    private String title;
    private String description;
    private int dueDays;
}
