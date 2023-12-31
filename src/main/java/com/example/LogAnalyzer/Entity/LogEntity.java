package com.example.LogAnalyzer.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "loganalyzer")
public class LogEntity {

    @Id
    @Field(type= FieldType.Keyword)
    private String id;

    @Field(type= FieldType.Date)
    private Date timestamp;

    @Field(type= FieldType.Keyword)
    private String source;

//    @Field(type = FieldType.Text)
    private String message;
    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
