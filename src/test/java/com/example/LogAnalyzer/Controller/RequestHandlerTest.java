package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LoggerRepository;
import com.example.LogAnalyzer.Service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;


import java.text.ParseException;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class RequestHandlerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LogService logService;


    @MockBean
    private LoggerRepository loggerRepository;


    @Test
    public void testSearch() throws Exception {
        List<LogEntity> logs = Collections.singletonList(new LogEntity());
        when(logService.search()).thenReturn(logs);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "search");

        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(logs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testSearchPage() throws Exception {
        List<LogEntity> logs = Collections.singletonList(new LogEntity());
        when(logService.searchUsingPage()).thenReturn(logs);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "search_paging");

        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(logs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testSearchScroll() throws Exception {
        List<LogEntity> logs = Collections.singletonList(new LogEntity());
        when(logService.searchUsingScroll()).thenReturn(logs);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "search_scroll");

        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(logs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testGroupBy() throws Exception {

        String fieldValue = "example_field";


        Map<String, Long> expectedGroupedData = new HashMap<>();
        expectedGroupedData.put("value1", 10L);
        expectedGroupedData.put("value2", 20L);
        when(logService.groupByDynamic(fieldValue)).thenReturn(expectedGroupedData);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "groupBy");
        requestBody.put("field", fieldValue);

        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedGroupedData)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testProjectBy() throws Exception {


        List<Map<String, Object>> expectedLogs = Arrays.asList(new HashMap<>(), new HashMap<>());
        when(logService.projectByDynamic("field1", "field2")).thenReturn(expectedLogs);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "projectBy");
        requestBody.put("fields", Arrays.asList("field1", "field2"));


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedLogs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    @Test
    public void testTimeFilter() throws Exception {

        String start = "2023-06-16T17:52:14.691Z";
        String end = "2023-06-16T17:52:14.692Z";
        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        when(logService.filterBytime(eq(start), eq(end))).thenReturn(expectedLogs);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "timeFilter");
        requestBody.put("starttime", start);
        requestBody.put("endtime", end);


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedLogs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testTermsFilter() throws Exception {


        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        try {
            when(logService.filterByTermsDynamic("example_field", "term1", "term2")).thenReturn(expectedLogs);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "termsFilter");
        requestBody.put("field", "example_field");
        requestBody.put("terms", Arrays.asList("term1", "term2"));


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedLogs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void nestedGroupBy() throws Exception {


        Map<String, List<Map<String, Long>>> nestedMap = new HashMap<>();
        List<Map<String, Long>> innerList = new ArrayList<>();
        Map<String, Long> innerMap = new HashMap<>();
        innerMap.put("value1", 10L);
        innerMap.put("value2", 20L);
        innerList.add(innerMap);
        nestedMap.put("key1", innerList);

        when(logService.nestedGroupByDynamic(anyString(), anyString())).thenReturn(nestedMap);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "nestedGroupBy");
        requestBody.put("field1", "field1");
        requestBody.put("field2", "field2");


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"key1\":[{\"value1\":10,\"value2\":20}]}"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testCardinality() throws Exception {


        String fieldValue = "example_field";
        Long expectedCardinality = 10L;
        when(logService.cardinalityAggs(fieldValue)).thenReturn(expectedCardinality);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "cardinality");
        requestBody.put("field", fieldValue);


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedCardinality.toString()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testerror() throws Exception {


        String fieldValue = "example_field";
        Long expectedCardinality = 10L;
        when(logService.cardinalityAggs(fieldValue)).thenReturn(expectedCardinality);


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", "nonvalidfield");
        requestBody.put("field", fieldValue);


        try {
            mockMvc.perform(post("/api")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}