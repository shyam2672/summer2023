package com.example.LogAnalyzer.Controller;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class LogControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LogService logService;

//    @InjectMocks
//    private LogController logController;
    @Test
    void getData() {
        // Arrange
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        when(logService.search()).thenReturn(expectedLogs);
//
//        ModelAndView expectedModelAndView = new ModelAndView("logs");
//        expectedModelAndView.addObject("logs", expectedLogs);
//
//        // Act
//        ModelAndView actualModelAndView = logController.getData();
//
//        // Assert
//        verify(logService).search();
//        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
//        assertEquals(expectedModelAndView.getModel(), actualModelAndView.getModel());

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
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        when(logService.searchUsingPage()).thenReturn(expectedLogs);
//
//        ModelAndView expectedModelAndView = new ModelAndView("logs");
//        expectedModelAndView.addObject("logs", expectedLogs);
//
//        // Act
//        ModelAndView actualModelAndView = logController.getDataPaging();
//
//        // Assert
//        verify(logService).searchUsingPage();
//        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
//        assertEquals(expectedModelAndView.getModel(), actualModelAndView.getModel());
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
//
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        when(logService.searchUsingScroll()).thenReturn(expectedLogs);
//
//        ModelAndView expectedModelAndView = new ModelAndView("logs");
//        expectedModelAndView.addObject("logs", expectedLogs);
//
//        // Act
//        ModelAndView actualModelAndView = logController.getDataScroll();
//
//        // Assert
//        verify(logService).searchUsingScroll();
//        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
//        assertEquals(expectedModelAndView.getModel(), actualModelAndView.getModel());
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
//        String fieldValue = "exampleField";
//        Long expectedCardinality = 10L;
//        when(logService.cardinalityAggs(eq(fieldValue))).thenReturn(expectedCardinality);
//
//        // Act
//        Long actualCardinality = logController.getCardinality(fieldValue);
//
//        // Assert
//        verify(logService).cardinalityAggs(eq(fieldValue));
//        assertEquals(expectedCardinality, actualCardinality);
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
//        String start = "2023-06-16T17:52:14.691Z";
//        String end = "2023-06-16T17:52:14.692Z";
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        when(logService.filterBytime(eq(start), eq(end))).thenReturn(expectedLogs);
//
//        ModelAndView expectedModelAndView = new ModelAndView("logs");
//        expectedModelAndView.addObject("logs", expectedLogs);
//
//        // Act
//        ModelAndView actualModelAndView = logController.filterByTime(start, end);
//
//        // Assert
//        verify(logService).filterBytime(eq(start), eq(end));
//        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
//        assertEquals(expectedModelAndView.getModel(), actualModelAndView.getModel());

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
//        // Arrange
//        String field = "exampleField";
//        List<String> terms = Arrays.asList("term1", "term2", "term3");
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("field", field);
//        requestBody.put("terms", terms);
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        try {
//            when(logService.filterByTermsDynamic(eq(field), any(String[].class))).thenReturn(expectedLogs);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Act
//        List<LogEntity> actualLogs = logController.filterByField(requestBody);
//
//        // Assert
//        try {
//            verify(logService).filterByTermsDynamic(eq(field), any(String[].class));
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        assertEquals(expectedLogs, actualLogs);
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
//        // Arrange
//        String fieldValue = "exampleField";
//        Map<String, Long> expectedGroupedData = new HashMap<>();
//        expectedGroupedData.put("group1", 10L);
//        expectedGroupedData.put("group2", 20L);
//        when(logService.groupByDynamic(eq(fieldValue))).thenReturn(expectedGroupedData);
//
//        ModelAndView expectedModelAndView = new ModelAndView("GroupByResults");
//        expectedModelAndView.addObject("grouped", expectedGroupedData);
//
//        // Act
//        ModelAndView actualModelAndView = logController.groupBy(fieldValue);
//
//        // Assert
//        verify(logService).groupByDynamic(eq(fieldValue));
//        assertEquals(expectedModelAndView.getViewName(), actualModelAndView.getViewName());
//        assertEquals(expectedModelAndView.getModel(), actualModelAndView.getModel());
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
//        List<String> terms = Arrays.asList("term1", "term2", "term3");
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("terms", terms);
//        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
//        when(logService.projectByDynamic(any(String[].class))).thenReturn(expectedLogs);
//
//        // Act
//        List<LogEntity> actualLogs = logController.projectby(requestBody);
//
//        // Assert
//        verify(logService).projectByDynamic(any(String[].class));
//        assertEquals(expectedLogs, actualLogs);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("terms", Arrays.asList("field1", "field2"));

        List<LogEntity> expectedLogs = Arrays.asList(new LogEntity(), new LogEntity());
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
}