package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LogService {

    List<LogEntity> savelogdata();

    List<LogEntity> search();

    Map<String, Long> groupBysource();

    Map<String, Long> groupByDynamic(String field);


    List<LogEntity> projectBySourceAndMessage();



    //fitler docs in given time range

    //fitler docs in given time range
    List<LogEntity> filterBytime(String start, String end);

    List<LogEntity> filterByterms();

    List<LogEntity> searchUsingPage();

    List<LogEntity> searchUsingScroll();


   Map<String, Long> tabularAggregation();
    Map<String, Long> nestedAggregation();

    Long cardinalityAggs(String field);

    List<LogEntity> filterByTermsDynamic(String field, String ...terms) throws ParseException;

    List<LogEntity> projectByDynamic(String... fields);
}
