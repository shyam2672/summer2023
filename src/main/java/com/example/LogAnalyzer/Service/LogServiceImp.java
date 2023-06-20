package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Helper.ExceltoEs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImp implements LogService{


    private final ExceltoEs helper;
    @Autowired
    public LogServiceImp(ExceltoEs helper){
        this.helper=helper;
    }

    @Override
    public void savelogdata() {
           helper.WriteToEs(helper.ReadFromExcel());
    }
}
