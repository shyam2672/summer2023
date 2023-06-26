package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LogServiceImpTest {

    @InjectMocks
    private LogServiceImp logService;

    @Mock
            private LogRepository logRepository;

    @Mock
            private ExceltoEs helper;


    @Mock
    private RestHighLevelClient client;
    private static List<LogEntity> logs = new ArrayList<>();


    @BeforeAll
    public static void initlogs(){
        LogEntity log1 = new LogEntity();
        log1.setID(String.valueOf(1));
        LocalDateTime date;
        String datetime="2023-06-16T11:22:14";
        date = LocalDateTime.parse(datetime);
        log1.setTimestamp(date);
        log1.setSource("source");
        log1.setMessage("message");
        LogEntity log2 = new LogEntity();

        log2.setID(String.valueOf(2));
        log2.setTimestamp(date);
        log2.setSource("source2");
        log2.setMessage("message2");
        logs.add(log1);
        logs.add(log2);
    }


    @Test
    public void saveTest(){

        when(helper.ReadFromExcel()).thenReturn(logs);
        when(helper.WriteToEs(any(LogRepository.class),anyList())).thenReturn(logs);

            assertEquals(logs,logService.savelogdata());

    }

    @Test
    public void searchTest() {

        when(logRepository.findAll()).thenReturn(logs);
        List<LogEntity> actualLogs = logService.search();
        assertEquals(logs, actualLogs);
    }


    @Test
    public void groupByTest(){


        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);

        Terms sourceaggs = mock(Terms.class);
        when(searchResponse.getAggregations()).thenReturn(aggs);
        Terms.Bucket bucket1 = mock(Terms.Bucket.class);
        when(bucket1.getKeyAsString()).thenReturn("source1");
        when(bucket1.getDocCount()).thenReturn(10L);
        Terms.Bucket bucket2 = mock(Terms.Bucket.class);
        when(bucket2.getKeyAsString()).thenReturn("source2");
        when(bucket2.getDocCount()).thenReturn(20L);
        List<Terms.Bucket> terms= new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);
        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();
        when(aggs.get("sources")).thenReturn(sourceaggs);

        try {
            when(client.search( any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<String, Long> result = logService.groupBysource();
//
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(searchResponse).getAggregations();
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(10L), result.get("source1"));
        assertEquals(Long.valueOf(20L), result.get("source2"));

    }

    @Test
    public void ProjecttionTest(){
        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");

        when(searchResponse.getHits()).thenReturn(searchHits);

        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());

        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<LogEntity> logs = logService.projectBySourceAndMessage();

        assertEquals(1, logs.size());
        assertEquals("source1", logs.get(0).getSource());
        assertEquals("message1", logs.get(0).getMessage());

    }


    @Test
    public void filterByTimeTest(){
        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        String start = "2023-06-16T11:22:14";
        String end = "2024-06-16T11:22:14";
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", LocalDateTime.parse(start));
        sourceAsMap.put("source","source1");
        sourceAsMap.put("message","message1");


        when(searchResponse.getHits()).thenReturn(searchHits);

        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());

        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        List<LogEntity> logs = logService.filterBytime(LocalDateTime.parse(start), LocalDateTime.parse(end));

        assertEquals(1, logs.size());
        assertEquals("1", logs.get(0).getID());
    }


    @Test
    public void filterByTermsTest()
    {
        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", LocalDateTime.now());
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");


        when(searchResponse.getHits()).thenReturn(searchHits);

        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());

        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<LogEntity> logs = logService.filterByterms();
        assertEquals("source1", logs.get(0).getSource());
    }



}