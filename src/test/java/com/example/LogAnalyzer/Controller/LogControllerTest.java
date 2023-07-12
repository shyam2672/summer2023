package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LoggerRepository;
import com.example.LogAnalyzer.Service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public
class LogControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LogService logService;

    @MockBean
    private LoggerRepository loggerRepository;


    @Test
    void getData() {


        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        when(logService.search()).thenReturn(expectedLogs);


        try {
            this.mockMvc.perform(get("/api/log/search")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(view().name("logs"))
                    .andExpect(model().attribute("logs", expectedLogs));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }


    @Test
    void getDataPaging() {

        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        when(logService.searchUsingPage()).thenReturn(expectedLogs);

        try {
            this.mockMvc.perform(get("/api/log/search/using_page")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(view().name("logs"))
                    .andExpect(model().attribute("logs", expectedLogs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
    @Test
    void getDataScroll() {

        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        when(logService.searchUsingScroll()).thenReturn(expectedLogs);

        try {
            this.mockMvc.perform(get("/api/log/search/using_scroll")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(view().name("logs"))
                    .andExpect(model().attribute("logs", expectedLogs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
    @Test
    void getCardinality() {

        String fieldValue = "example_field";
        Long expectedCardinality = 10L;
        when(logService.cardinalityAggs(fieldValue)).thenReturn(expectedCardinality);

        try {
            mockMvc.perform(get("/api/log/cardinality")
                            .param("fieldValue", fieldValue)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedCardinality.toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void filterByTime() {

        String start = "2023-06-16T17:52:14.691Z";
        String end = "2023-06-16T17:52:14.692Z";
        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        when(logService.filterBytime(eq(start), eq(end))).thenReturn(expectedLogs);

        try {
            this.mockMvc.perform(get("/api/log/filterByTime").param("start", start)
                            .param("end", end)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(view().name("logs"))
                    .andExpect(model().attribute("logs", expectedLogs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //
    @Test
    void filterByField() {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("field", "example_field");
        requestBody.put("terms", Arrays.asList("term1", "term2"));

        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
        try {
            when(logService.filterByTermsDynamic("example_field", "term1", "term2")).thenReturn(expectedLogs);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            mockMvc.perform(post("/api/log/filterByTerms")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedLogs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
    @Test
    void groupBy() {

        String fieldValue = "example_field";
        Map<String, Long> expectedGroupedData = new HashMap<>();
        expectedGroupedData.put("value1", 10L);
        expectedGroupedData.put("value2", 20L);

        when(logService.groupByDynamic(fieldValue)).thenReturn(expectedGroupedData);

        try {
            mockMvc.perform(get("/api/log/groupBy")
                            .param("fieldValue", fieldValue)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(view().name("GroupByResults"))
                    .andExpect(model().attribute("grouped", expectedGroupedData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
    @Test
    void projectby() {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("terms", Arrays.asList("field1", "field2"));

        List<Map<String, Object>> expectedLogs = Arrays.asList(new HashMap<>(), new HashMap<>());
        when(logService.projectByDynamic("field1", "field2")).thenReturn(expectedLogs);

        try {
            mockMvc.perform(post("/api/log/projectBy")
                            .content(new ObjectMapper().writeValueAsString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedLogs)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void nestedGroupBy() {
        Map<String, List<Map<String, Long>>> nestedMap = new HashMap<>();
        List<Map<String, Long>> innerList = new ArrayList<>();
        Map<String, Long> innerMap = new HashMap<>();
        innerMap.put("value1", 10L);
        innerMap.put("value2", 20L);
        innerList.add(innerMap);
        nestedMap.put("key1", innerList);

        when(logService.nestedGroupByDynamic(anyString(), anyString())).thenReturn(nestedMap);


        try {
            mockMvc.perform(get("/api/log/nestedGroupBy")
                            .param("field1Value", "value1")
                            .param("field2Value", "value2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("{\"key1\":[{\"value1\":10,\"value2\":20}]}"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}