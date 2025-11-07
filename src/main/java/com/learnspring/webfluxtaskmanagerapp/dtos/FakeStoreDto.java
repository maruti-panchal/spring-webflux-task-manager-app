package com.learnspring.webfluxtaskmanagerapp.dtos;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "fakestore")
public class FakeStoreDto {
    @Id
    public String id;
    public String title;
    public double price;
    public String description;
    public String category;
    public String image;
}
