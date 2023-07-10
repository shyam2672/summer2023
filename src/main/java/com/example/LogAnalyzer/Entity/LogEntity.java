package com.example.LogAnalyzer.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Date;

@Document(indexName = "loganalyzer")
public class
LogEntity {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Date)
    private Date timestamp;

    @Field(type = FieldType.Keyword)
    private String source;
    @Field(type = FieldType.Date)
    private LocalDate date;

    //    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Keyword)
    private String loglevel;

    @Field(type = FieldType.Keyword)
    private String logger;

    @Field(type = FieldType.Keyword)
    private String partnerid;


    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


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

    @Override
    public String toString() {
        return "LogEntity{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                ", date=" + date +
                ", message='" + message + '\'' +
                '}';
    }
}


