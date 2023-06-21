package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;

import java.util.List;

public interface LogService {

    List<LogEntity> savelogdata();

    List<LogEntity> search();
}
