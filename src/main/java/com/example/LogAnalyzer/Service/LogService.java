package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface LogService {

    List<LogEntity> savelogdata();

    List<LogEntity> search();

    List<LogEntity> groupBysource();


    List<LogEntity> projectBySourceAndMessage();

    List<LogEntity> filterBytime(LocalDateTime start,LocalDateTime end);

    List<LogEntity> filterByterms();

}
