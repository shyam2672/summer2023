package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public void getData(){
            service.search();
    }
}
