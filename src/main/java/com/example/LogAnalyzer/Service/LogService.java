package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LogService {

    List<LogEntity> savelogdata();

    List<LogEntity> search();

    Map<String, Long> groupBysource();


    List<LogEntity> projectBySourceAndMessage();

    List<LogEntity> filterBytime(LocalDateTime start,LocalDateTime end);

    List<LogEntity> filterByterms();

}
