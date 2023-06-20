package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log")
public class LogController {
    private final LogService service;

    @Autowired
    public LogController(LogService service) {
        this.service = service;
    }

    @PostMapping
    public void savedata() {
        service.savelogdata();
    }
}
