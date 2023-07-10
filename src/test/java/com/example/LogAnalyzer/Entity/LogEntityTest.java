package com.example.LogAnalyzer.Entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogEntityTest {

    private static LogEntity logEntity;

    @BeforeAll
    public static void setUp() {
        logEntity = new LogEntity();
    }

    @Test
    public void testGetSetID() {
        String id = "test-id";
        logEntity.setID(id);
        assertEquals(id, logEntity.getID());
    }

    @Test
    public void testGetSetloglevel() {
        String loglevel = "test";
        logEntity.setLoglevel(loglevel);
        assertEquals(loglevel, logEntity.getLoglevel());
    }

    @Test
    public void testGetSetlogger() {
        String logger = "logger";
        logEntity.setLogger(logger);
        assertEquals(logger, logEntity.getLogger());
    }

    @Test
    public void testGetSetpartnerid() {
        String id = "test";
        logEntity.setPartnerid(id);
        assertEquals(id, logEntity.getPartnerid());
    }

    @Test
    public void testGetSetTimestamp() {
        Date timestamp = new Date();
        logEntity.setTimestamp(timestamp);
        assertEquals(timestamp, logEntity.getTimestamp());
    }

    @Test
    public void testGetSetSource() {
        String source = "test-source";
        logEntity.setSource(source);
        assertEquals(source, logEntity.getSource());
    }

    @Test
    public void testGetSetDate() {
        LocalDate date = LocalDate.now();
        logEntity.setDate(date);
        assertEquals(date, logEntity.getDate());
    }

    @Test
    public void testGetSetMessage() {
        String message = "test-message";
        logEntity.setMessage(message);
        assertEquals(message, logEntity.getMessage());
    }

    @Test
    public void testToString() {
        String id = "test-id";
        Date timestamp = new Date();
        String source = "test-source";
        LocalDate date = LocalDate.now();
        String message = "test-message";

        logEntity.setID(id);
        logEntity.setTimestamp(timestamp);
        logEntity.setSource(source);
        logEntity.setDate(date);
        logEntity.setMessage(message);

        String expected = "LogEntity{id='test-id', timestamp=" + timestamp.toString() + ", source='test-source', date=" + date.toString() + ", message='test-message'}";
        assertEquals(expected, logEntity.toString());
    }
}