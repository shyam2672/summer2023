package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface LogService {

    List<LogEntity> savelogdata();

    List<LogEntity> search();

    Map<String, Long> groupBysource();

    Map<String, Long> groupByDynamic(String field);


    List<LogEntity> projectBySourceAndMessage();


    List<LogEntity> filterBytime(String start, String end);

    List<LogEntity> filterByterms();

    List<LogEntity> searchUsingPage();

    List<LogEntity> searchUsingScroll();


    Map<String, Long> tabularAggregation();

    Map<String, Long> nestedAggregation();

    Long cardinalityAggs(String field);

    List<LogEntity> filterByTermsDynamic(String field, String... terms) throws ParseException;

    List<Map<String, Object>> projectByDynamic(String... fields);

    Map<String, List<Map<String, Long>>> nestedGroupByDynamic(String field, String field2);

}
