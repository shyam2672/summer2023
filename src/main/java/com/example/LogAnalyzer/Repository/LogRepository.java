package com.example.LogAnalyzer.Repository;

import com.example.LogAnalyzer.Entity.LogEntity;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface
LogRepository extends ElasticsearchRepository<LogEntity, Integer> {

}
