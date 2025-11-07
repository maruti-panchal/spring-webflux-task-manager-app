package com.learnspring.webfluxtaskmanagerapp.repository;

import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Productrepository extends ReactiveElasticsearchRepository<FakeStoreDto,String> {
}
