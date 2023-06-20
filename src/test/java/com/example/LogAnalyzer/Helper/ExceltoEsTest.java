package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExceltoEsTest {


    @Autowired
    private ExceltoEs helper;

    @Autowired
    private LogRepository logRepository;

    static  List<LogEntity>  logs;
    @Test
    public  void ReadTest(){
         assertDoesNotThrow(
                 ()->{
                      logs= helper.ReadFromExcel();
                 }
         );
    }

    @Test
    public void WriteTest(){
          assertEquals(logs,helper.WriteToEs(logRepository,logs));


    }

    @Test
    public void validateFileTest(){
        assertTrue(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx"));
        assertFalse(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlx"));
    }





}