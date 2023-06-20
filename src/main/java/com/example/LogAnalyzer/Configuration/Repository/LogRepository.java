package com.example.LogAnalyzer.Configuration.Repository;

import com.example.LogAnalyzer.Configuration.Entity.LogEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ElasticsearchRepository<LogEntity, Integer> {

}
