package com.example.LogAnalyzer.Repository;

import com.example.LogAnalyzer.Entity.LoggerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggerRepository extends MongoRepository<LoggerEntity, String> {

}
