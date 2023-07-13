package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Service.LogService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.example.LogAnalyzer")
public class LogAnalyzerApplication {


    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LogAnalyzerApplication.class, args);


        LogService obj = context.getBean(LogService.class);
//obj.savelogdata();

    }


}
