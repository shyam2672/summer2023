package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
    public ModelAndView getData(){
//            model.addAttribute("logs",logs);
        ModelAndView modelAndView=new ModelAndView("logs");
            List<LogEntity> logs=service.search();
      modelAndView.addObject("logs",logs);
        System.out.println("hiii");
            return modelAndView;
    }
}
