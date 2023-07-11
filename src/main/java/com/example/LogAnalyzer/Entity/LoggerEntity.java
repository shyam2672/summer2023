package com.example.LogAnalyzer.Entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "validLogger")
public class LoggerEntity {
    @Id
    private String id;


    private String logger;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getlogger() {
        return logger;
    }

    public void setlogger(String logger) {
        this.logger = logger;
    }
}
