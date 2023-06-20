package com.example.LogAnalyzer.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "loganalyzer")
public class LogEntity {

    @Id
//    @Field(type= FieldType.Keyword)
    private long id;

    private String timestamp;

    private String source;

//    @Field(type = FieldType.Text)
    private String message;
    public long getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
