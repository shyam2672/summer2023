package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = LogAnalyzerApplication.class)
public class LogAnalyzerApplicationTest {

    @Autowired
    private LogService logService;

    @Autowired
    private ExceltoEs helper;

    @Test
    public void testLogService() {
        assertNotNull(logService);
        assertNotNull(helper);
    }
}