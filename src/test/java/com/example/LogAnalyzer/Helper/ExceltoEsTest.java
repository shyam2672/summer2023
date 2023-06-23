package com.example.LogAnalyzer.Helper;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
//@TestPropertySource(properties = "logdata=/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx")
class ExceltoEsTest {


//    @Autowired
    @InjectMocks
    private ExceltoEs helper=new ExceltoEs();

    @Mock
    private LogRepository logRepository;

    @Test
    public  void ReadTest(){
         assertDoesNotThrow(
                 ()->{
                      helper.ReadFromExcel();
                 }
         );
    }

    @Test
    public void WriteTest(){

        List<LogEntity> logs = new ArrayList<>();
        LogEntity log1 = new LogEntity();
        log1.setID(String.valueOf(1));
        Date date;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    .parse("16/06/2023 11:22:15");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log1.setTimestamp(date);
        log1.setSource("source");
        log1.setMessage("message");
        LogEntity log2 = new LogEntity();

        log2.setID(String.valueOf(2));
        log2.setTimestamp(date);
        log2.setSource("source");
        log2.setMessage("message");

        logs.add(log1);
        logs.add(log2);
            when(logRepository.saveAll(anyList())).thenReturn(logs);

          assertEquals(logs,helper.WriteToEs(logRepository,logs));


    }

    @Test
    public void validateFileTest(){
        assertTrue(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlsx"));
        assertFalse(helper.validate("/Users/shyamprajapati/Downloads/LogAnalyzer/src/main/resources/static/logsdata.xlx"));
    }





}