package com.example.LogAnalyzer.Controller;


import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RequestHandler {
    private final LogService service;

    @Autowired
    public RequestHandler(LogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> processRequest(@RequestBody Map<String, Object> requestBody) {
        String type = (String) requestBody.get("query");
        if (type.equals("search")) {
            List<LogEntity> logs = service.search();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } else if (type.equals("search_paging")) {
            List<LogEntity> logs = service.searchUsingPage();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } else if (type.equals("search_scroll")) {
            List<LogEntity> logs = service.searchUsingScroll();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } else if (type.equals("groupBy")) {
            String field = (String) requestBody.get("field");
            Map<String, Long> mp = service.groupByDynamic(field);
            return new ResponseEntity<>(mp, HttpStatus.OK);

        } else if (type.equals("projectBy")) {
            List<String> fields = (List<String>) requestBody.get("fields");
            List<Map<String, Object>> response = service.projectByDynamic(fields.toArray(new String[0]));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else if (type.equals("timeFilter")) {
            String start = (String) requestBody.get("starttime");
            String end = (String) requestBody.get("endtime");
            List<LogEntity> logs = service.filterBytime(start, end);
            return new ResponseEntity<>(logs, HttpStatus.OK);

        } else if (type.equals("termsFilter")) {
            String field = (String) requestBody.get("field");
            List<String> terms = (List<String>) requestBody.get("terms");
            try {
                List<LogEntity> logs = service.filterByTermsDynamic(field, terms.toArray(new String[0]));
                return new ResponseEntity<>(logs, HttpStatus.OK);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


        } else if (type.equals("nestedGroupBy")) {
            String field1 = (String) requestBody.get("field1");
            String field2 = (String) requestBody.get("field2");
            Map<String, List<Map<String, Long>>> response = service.nestedGroupByDynamic(field1, field2);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else if (type.equals("cardinality")) {
            String field = (String) requestBody.get("field");

            Long cardinality = service.cardinalityAggs(field);
            return new ResponseEntity<>(cardinality, HttpStatus.OK);

        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
