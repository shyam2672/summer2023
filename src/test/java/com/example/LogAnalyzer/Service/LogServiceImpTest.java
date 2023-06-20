package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LogServiceImpTest {

    @Autowired
    private LogService logService;

    int f=0;
    @Test
    public void saveTest(){
        assertDoesNotThrow(()->{
            f=logService.savelogdata().size();
        });
    }

    @Test
    public void searchTest(){
        List<LogEntity> f1,f2;
        f1=logService.savelogdata();
        f2=logService.search();
         for(int i=0;i<f1.size();i++)
         {
             assertEquals(f1.get(i).getID(),f2.get(i).getID());
             assertEquals(f1.get(i).getTimestamp(),f2.get(i).getTimestamp());
             assertEquals(f1.get(i).getSource(),f2.get(i).getSource());
             assertEquals(f1.get(i).getMessage(),f2.get(i).getMessage());

         }
    }

}