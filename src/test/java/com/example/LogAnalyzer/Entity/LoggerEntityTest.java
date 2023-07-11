package com.example.LogAnalyzer.Entity;

import com.example.LogAnalyzer.Entity.LoggerEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class LoggerEntityTest {

    @Test
    public void testGettersAndSetters() {
        LoggerEntity loggerEntity = new LoggerEntity();
        loggerEntity.setId("1");
        loggerEntity.setlogger("test_logger");

        Assertions.assertEquals("1", loggerEntity.getId());
        Assertions.assertEquals("test_logger", loggerEntity.getlogger());
    }
}