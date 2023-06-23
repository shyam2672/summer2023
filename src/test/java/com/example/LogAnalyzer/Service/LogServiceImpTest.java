package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LogServiceImpTest {

    @InjectMocks
    private LogServiceImp logService;

    @Mock
            private LogRepository logRepository;

    @Mock
            private ExceltoEs helper;
    int f=0;





    @Test
    public void saveTest(){

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

        when(helper.ReadFromExcel()).thenReturn(logs);
        when(helper.WriteToEs(any(LogRepository.class),anyList())).thenReturn(logs);

            assertEquals(logs,logService.savelogdata());

    }

    @Test
    public void searchTest() {
//        List<LogEntity> f1,f2;
//        f1=logService.savelogdata();
//        f2=logService.search();
//         for(int i=0;i<f1.size();i++)
//         {
//             assertEquals(f1.get(i).getID(),f2.get(i).getID());
//             assertEquals(f1.get(i).getTimestamp(),f2.get(i).getTimestamp());
//             assertEquals(f1.get(i).getSource(),f2.get(i).getSource());
//             assertEquals(f1.get(i).getMessage(),f2.get(i).getMessage());
//
//         }

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
        when(logRepository.findAll()).thenReturn(logs);
        List<LogEntity> actualLogs = logService.search();
        assertEquals(logs, actualLogs);
    }

}