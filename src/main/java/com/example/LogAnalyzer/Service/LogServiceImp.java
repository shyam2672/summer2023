package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class LogServiceImp implements LogService{


    private final LogRepository logRepository;

    private final ExceltoEs helper;
    @Autowired
    public LogServiceImp(ExceltoEs helper,LogRepository logRepository){
        this.helper=helper;
        this.logRepository=logRepository;
    }

    @Override
    public List<LogEntity> savelogdata() {
           return helper.WriteToEs(logRepository,helper.ReadFromExcel());
    }

    @Override
    public List<LogEntity> search() {
//        Page<LogEntity> documentsPage = logRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));
//        List<LogEntity> documents = documentsPage.getContent();
//
//        for (LogEntity document : documents) {
//            System.out.println(document.getSource());
//        }

        Iterable<LogEntity> logs = logRepository.findAll();
        List<LogEntity> loggs=new ArrayList<>();
        for (LogEntity log : logs) {
         loggs.add(log);
//            System.out.println(log.getMessage());
        }



//        Collections.sort(loggs, new Comparator<LogEntity>() {
//
//            public int compare(LogEntity o1, LogEntity o2) {
//                // compare two instance of `Score` and return `int` as result.
//                return Long.compare(o1.getID(), o2.getID());
//            }
//        });


        return loggs;
//


//        return f;

    }
}
