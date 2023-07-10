package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/log")
public class LogController {
    private final LogService service;

    @Autowired
    public LogController(LogService service) {
        this.service = service;
    }



    @GetMapping("/search")
    public ModelAndView getData(){
//            model.addAttribute("logs",logs);
        ModelAndView modelAndView=new ModelAndView("logs");
            List<LogEntity> logs=service.search();
      modelAndView.addObject("logs",logs);
            return modelAndView;
    }

    @GetMapping("/search/using_page")
    public ModelAndView getDataPaging(){
//            model.addAttribute("logs",logs);
        ModelAndView modelAndView=new ModelAndView("logs");
        List<LogEntity> logs=service.searchUsingPage();
        modelAndView.addObject("logs",logs);
        return modelAndView;
    }

    @GetMapping("/search/using_scroll")
    public ModelAndView getDataScroll(){
//            model.addAttribute("logs",logs);
        ModelAndView modelAndView=new ModelAndView("logs");
        List<LogEntity> logs=service.searchUsingScroll();
        modelAndView.addObject("logs",logs);
        return modelAndView;
    }

//    http://localhost:8080/api/log/cardinality?fieldValue=source
    @GetMapping("/cardinality")
    public Long getCardinality(@RequestParam("fieldValue") String fieldValue) {
       return service.cardinalityAggs(fieldValue);
    }


//    http://localhost:8080/api/log/filterByTime?start=2023-06-16T17:52:14.691Z&end=2023-06-16T17:52:14.692Z
    @GetMapping("/filterByTime")
    public ModelAndView filterByTime(@RequestParam("start") String start,@RequestParam("end") String end){
        ModelAndView modelAndView=new ModelAndView("logs");
        List<LogEntity> logs=service.filterBytime(start,end);
        System.out.println(1);
        System.out.println(start + " " + end);
        System.out.println(logs.size());
        modelAndView.addObject("logs",logs);
        return modelAndView;
    }

    @PostMapping("/filterByTerms")
    public List<LogEntity> filterByField(@RequestBody Map<String, Object> requestBody) {
        String field = (String) requestBody.get("field");
        System.out.println(field);
        List<String> terms = (List<String>) requestBody.get("terms");
        try {
            List<LogEntity> logs = service.filterByTermsDynamic(field, terms.toArray(new String[0]));
            System.out.println(logs.size());
            return logs;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

//    http://localhost:8080/api/log/groupBy?fieldValue=source
    @GetMapping("/groupBy")
    public ModelAndView groupBy(@RequestParam("fieldValue") String fieldValue) {
        ModelAndView modelAndView=new ModelAndView("GroupByResults");
        Map<String, Long> groupedData=service.groupByDynamic(fieldValue);
        modelAndView.addObject("grouped",groupedData);
        return modelAndView;
    }


    @PostMapping("/projectBy")
    public List<Map<String, Object>> projectby(@RequestBody Map<String, Object> requestBody){
        List<String> terms = (List<String>) requestBody.get("terms");
        List<Map<String, Object>> logs = service.projectByDynamic(terms.toArray(new String[0]));
        return logs;

    }



}
