package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExceltoEsTest {


    @Autowired
    private ExceltoEs helper;

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

          assertEquals(logs,helper.WriteToEs(logs));

        
    }

    @Test
    public void validateFileTest(){
        assertTrue(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx"));
        assertFalse(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlx"));

    }





}