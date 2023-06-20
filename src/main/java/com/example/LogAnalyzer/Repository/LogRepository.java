package com.example.LogAnalyzer.Repository;

import com.example.LogAnalyzer.Entity.LogEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ElasticsearchRepository<LogEntity, Integer> {

}
