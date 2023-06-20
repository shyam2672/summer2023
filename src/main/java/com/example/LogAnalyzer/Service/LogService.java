package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LogService {
    List<LogEntity> savelogdata();

    List<LogEntity> search();
}
