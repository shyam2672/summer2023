package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.LogAnalyzerApplication;
import com.example.LogAnalyzer.Service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = LogAnalyzerApplication.class)
public class LogAnalyzerApplicationTest {

	@Autowired
	private LogService logService;

	@Test
	public void testLogService() {
		assertNotNull(logService);
	}
}