package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.SimpleDateFormat;
<<<<<<< HEAD
=======
import java.time.Instant;
import java.time.LocalDate;
>>>>>>> 58dc878 (dynamic termsfilter)
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Date date;


        date = new Date();
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
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");
        sourceAsMap.put("date",LocalDate.now());
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

<<<<<<< HEAD
=======
    @Test
    public void filterByTimeTestFail(){
        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        String start = "2023-06-16T11:22:14";
        String end = "2023-06-16T11:23:14";
        String now= "2024-06-16T11:22:14";
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T12:22:15.189Z");
        sourceAsMap.put("date",LocalDate.now());

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
   Date ts= logs.get(0).getTimestamp();
        System.out.println(ts);
        Instant instant = ts.toInstant();

// Convert Instant to LocalDateTime
        LocalDateTime tsp = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        boolean isAfter = tsp.isAfter(LocalDateTime.parse(start));
        boolean isBefore = tsp.isBefore(LocalDateTime.parse(end));
        assertFalse(isBefore && isAfter);

    }
>>>>>>> 58dc878 (dynamic termsfilter)

    @Test
    public void filterByTermsTest()
    {

        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
<<<<<<< HEAD
        sourceAsMap.put("timestamp", LocalDateTime.now());
=======
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");

        sourceAsMap.put("date", LocalDate.now());
        sourceAsMap.put("source", "standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7");
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
        assertEquals("standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7", logs.get(0).getSource());
    }

    @Test
    public void filterByTermsTestFail()
    {

        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");
        sourceAsMap.put("date",LocalDate.now());
>>>>>>> 58dc878 (dynamic termsfilter)
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

//    @Test
//    public void searchUsingPageTest(){
//        // Mock first page with 5 entities
//
//PageImpl<LogEntity> page1= new PageImpl<>(List.of(   new LogEntity(),
//        new LogEntity(),
//        new LogEntity(),
//        new LogEntity(),
//        new LogEntity()));
//
//
//        PageImpl<LogEntity> page2= new PageImpl<>(List.of(   new LogEntity(),
//                new LogEntity(),
//                new LogEntity(),
//                new LogEntity(),
//                new LogEntity()));
//
//
//        Page<LogEntity> firstPage =mock(Page.class);
//        Page<LogEntity> secondPage =mock(Page.class);
//
//
//        // Mock second page with 3 entities
//
//
//
//        Mockito.when(logRepository.findAll(any(Pageable.class)))
//                .thenReturn(firstPage);
//
//        when(firstPage.getContent()).thenReturn(page1.getContent());
//        when(secondPage.getPageable()).thenReturn(secondPage.getPageable());
//        when(firstPage.nextPageable()).thenReturn(secondPage.getPageable());
//
//        Mockito.when(logRepository.findAll(secondPage.getPageable()))
//                .thenReturn(secondPage);
//        when(secondPage.getContent()).thenReturn(page2.getContent());
//
//
//
//
//        // Call the method under test
//        List<LogEntity> logs = logService.searchUsingPage();
//
//        // Assert that 8 entities were returned
//        assertEquals(8, logs.size());
//
//
//    }

    @Test
    public void testSearchUsingPage() {
        Page<LogEntity> page1 = createLogEntityPage(1, 1000, true);
        Page<LogEntity> page2 = createLogEntityPage(1001, 2000, false);

        when(logRepository.findAll(any(Pageable.class))).thenReturn(page1, page2);

        List<LogEntity> expectedLogs = new ArrayList<>();
        expectedLogs.addAll(page1.getContent());
        expectedLogs.addAll(page2.getContent());

        List<LogEntity> actualLogs = logService.searchUsingPage();

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
    }

    private Page<LogEntity> createLogEntityPage(int startIndex, int endIndex, boolean hasNext) {
        List<LogEntity> logs = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            LogEntity log = new LogEntity();
            log.setID(String.valueOf(i));
            logs.add(log);
        }

        Page<LogEntity> page = Mockito.mock(Page.class);
        when(page.getContent()).thenReturn(logs);
        when(page.hasNext()).thenReturn(hasNext);
        when(page.nextPageable()).thenReturn(Pageable.unpaged());

        return page;
    }

    @Test
    public void searchUsingScrollTest(){
        SearchResponse searchResponse1 = mock(SearchResponse.class);
        SearchResponse searchResponse2 = mock(SearchResponse.class);
        SearchHits searchHits1 = mock(SearchHits.class);
        SearchHits searchHits2 = mock(SearchHits.class);
        SearchHit searchHit1 = mock(SearchHit.class);
        SearchHit searchHit2 = mock(SearchHit.class);
        Map<String, Object> sourceAsMap1 = new HashMap<>();
        Map<String, Object> sourceAsMap2 = new HashMap<>();
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(100);
        searchRequest.source(searchSourceBuilder);
        try {
            when(client.search(eq(searchRequest), eq(RequestOptions.DEFAULT))).thenReturn(searchResponse1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            when(client.scroll(any(SearchScrollRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(searchResponse2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        when(searchResponse1.getScrollId()).thenReturn("scrollId1");
        when(searchResponse1.getHits()).thenReturn(searchHits1);
        when(searchResponse2.getScrollId()).thenReturn("scrollId2");
        when(searchResponse2.getHits()).thenReturn(searchHits2);
        when(searchHits1.getHits()).thenReturn(new SearchHit[] { searchHit1 });
        when(searchHits2.getHits()).thenReturn(new SearchHit[] { });

        when(searchHits1.iterator()).thenReturn(List.of(searchHit1).iterator());

        sourceAsMap1.put("source", "source1");
        sourceAsMap1.put("message", "message1");
        sourceAsMap2.put("source", "source2");
        sourceAsMap2.put("message", "message2");
        when(searchHit1.getSourceAsMap()).thenReturn(sourceAsMap1);

        List<LogEntity> logs = logService.searchUsingScroll();

        List<LogEntity> expectedLogs = new ArrayList<>();
        LogEntity log1 = new LogEntity();
        log1.setID("1");
        log1.setSource("source1");
        log1.setMessage("message1");
        expectedLogs.add(log1);
        assertEquals(expectedLogs.size(),logs.size());
     for(int i=0;i<expectedLogs.size();i++){
         LogEntity logg1=expectedLogs.get(i);
         LogEntity logg2=logs.get(i);
         assertEquals(logg1.getMessage(),logg2.getMessage());
         assertEquals(logg1.getID(),logg2.getID());
         assertEquals(logg1.getSource(),logg2.getSource());

     }

    }

    @Test
    public void tabularAggTest(){
        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);

        Terms sourceaggs = mock(Terms.class);
        when(searchResponse.getAggregations()).thenReturn(aggs);
        Terms.Bucket bucket1 = mock(Terms.Bucket.class);
        when(bucket1.getKeyAsString()).thenReturn("source1");
        Terms.Bucket bucket2 = mock(Terms.Bucket.class);
        when(bucket2.getKeyAsString()).thenReturn("source2");
        List<Terms.Bucket> terms= new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);
        Cardinality uniqueTimestamps = mock(Cardinality.class);
        when( uniqueTimestamps.getValue()).thenReturn(100L);

        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();

        when(aggs.get("timestamps_per_source")).thenReturn(sourceaggs);
        when(bucket1.getAggregations()).thenReturn(aggs);
        when(bucket2.getAggregations()).thenReturn(aggs);
        when(aggs.get("unique_timestamps")).thenReturn(uniqueTimestamps);
//        when(bucket1.getAggregations().get("unique_timestamps")).thenReturn(uniqueTimestamps);
//        when(bucket2.getAggregations().get("unique_timestamps")).thenReturn(uniqueTimestamps);

        try {
            when(client.search( any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<String, Long> result = logService.tabularAggregation();
//
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(searchResponse).getAggregations();
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(100L), result.get("source1"));
        assertEquals(Long.valueOf(100L), result.get("source2"));


    }

    @Test
    public void nestedAggTest(){
        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);

        Terms sourcesAgg = mock(Terms.class);
        List<Terms.Bucket> sourcesBuckets = new ArrayList<>();

        Terms.Bucket sourcesBucket1 = mock(Terms.Bucket.class);

        when(sourcesBucket1.getKeyAsString()).thenReturn("source1");

        Terms.Bucket sourcesBucket2 = mock(Terms.Bucket.class);
        when(sourcesBucket2.getKeyAsString()).thenReturn("source2");
        sourcesBuckets.add(sourcesBucket1);
        sourcesBuckets.add(sourcesBucket2);


        doAnswer(invocation -> {
            return sourcesBuckets;
        }).when(sourcesAgg).getBuckets();


        Histogram timestampsAgg = mock(Histogram.class);
        List<Histogram.Bucket> timestampBuckets = new ArrayList<>();

        Histogram.Bucket timestampsBucket1 = mock(Histogram.Bucket.class);
        when(timestampsBucket1.getKeyAsString()).thenReturn("2019-01-01");

        Histogram.Bucket timestampsBucket2 = mock(Histogram.Bucket.class);
        when(timestampsBucket2.getKeyAsString()).thenReturn("2019-01-02");

        timestampBuckets.add(timestampsBucket1);
        timestampBuckets.add(timestampsBucket2);


        doAnswer(invocation -> {
            return timestampBuckets;
        }).when(timestampsAgg).getBuckets();

        Cardinality uniqueIds = mock(Cardinality.class);
        when(uniqueIds.getValue()).thenReturn(100L);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        when(searchResponse.getAggregations()).thenReturn(aggs);
        when(aggs.get("sources")).thenReturn(sourcesAgg);

        when(sourcesBucket1.getAggregations()).thenReturn(aggs);
        when(sourcesBucket2.getAggregations()).thenReturn(aggs);

        when(aggs.get("timestamps")).thenReturn(timestampsAgg);

        when(timestampsBucket1.getAggregations()).thenReturn(aggs);
        when(timestampsBucket2.getAggregations()).thenReturn(aggs);
        when(aggs.get("unique_ids")).thenReturn(uniqueIds);


        Map<String, Long> mp=logService.nestedAggregation();

        assertEquals(mp.get("source1-2019-01-01"),100L);
        assertEquals(mp.get("source2-2019-01-02"),100L);

    }

    @Test
    public void cardinalityaggTest(){

        // Mock search response
        SearchResponse searchResponse = mock(SearchResponse.class);
        Cardinality cardinalityAgg = mock(Cardinality.class);
        Aggregations aggs = mock(Aggregations.class);

        // Stub cardinality value
        when(cardinalityAgg.getValue()).thenReturn(100L);
when(searchResponse.getAggregations()).thenReturn(aggs);
        when(aggs.get("unique_" + "field"))
                .thenReturn(cardinalityAgg);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Call function under test
        long cardinality = logService.cardinalityAggs("field");

        // Assertions
        assertEquals(100L, cardinality);
    }


    @Test
    public void testFilterByTermsDynamic() throws Exception {
        String field = "source";
        String[] terms = { "app-1", "app-2" };
        String timestamp = "2023-06-30T12:34:56.789Z";
        String source = "app-1";
        String message = "message";
        Map<String, Object> sourceAsMap = Map.of("ID", "1", "timestamp", timestamp, "date", "2023-06-30", "source", source, "message", message);
        SearchHit hit = mock(SearchHit.class);
        when(hit.getSourceAsMap()).thenReturn(sourceAsMap);
        SearchHits hits = mock(SearchHits.class);
        when(hits.iterator()).thenReturn(List.of(hit).iterator());
        SearchResponse response = mock(SearchResponse.class);
        when(response.getHits()).thenReturn(hits);
        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(response);

        List<LogEntity> logs = logService.filterByTermsDynamic(field, terms);

        assertEquals(1, logs.size());
        LogEntity log = logs.get(0);
        assertEquals("1", log.getID());
        assertEquals(LocalDate.of(2023, 6, 30), log.getDate());
        assertEquals(source, log.getSource());
        assertEquals(message, log.getMessage());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setLenient(false);
        Date expectedTimestamp = formatter.parse(timestamp);
        assertEquals(expectedTimestamp, log.getTimestamp());

//        SearchRequest expectedRequest = new SearchRequest();
//        expectedRequest.indices("loganalyzer");
//        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(field, terms);
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(termsQuery);
//        expectedRequest.source(sourceBuilder);
//        verify(client).search(eq(expectedRequest), any(RequestOptions.class));
    }

    @Test
    public void testGroupByDynamic() throws IOException {

        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);
        Terms sourceaggs = mock(Terms.class);
        when(searchResponse.getAggregations()).thenReturn(aggs);
        Terms.Bucket bucket1 = mock(Terms.Bucket.class);
        when(bucket1.getKeyAsString()).thenReturn("field1");
        when(bucket1.getDocCount()).thenReturn(10L);
        Terms.Bucket bucket2 = mock(Terms.Bucket.class);
        when(bucket2.getKeyAsString()).thenReturn("field2");
        when(bucket2.getDocCount()).thenReturn(20L);
        List<Terms.Bucket> terms= new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);
        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();
        when(aggs.get("groupBy_field")).thenReturn(sourceaggs);

        try {
            when(client.search( any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<String, Long> result = logService.groupByDynamic("field");
//
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(searchResponse).getAggregations();
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(10L), result.get("field1"));
        assertEquals(Long.valueOf(20L), result.get("field2"));

    }


    @Test
    public void projectiondynamictest(){
        SearchResponse searchResponse = mock(SearchResponse.class);

        SearchHits searchHits = mock(SearchHits.class);

        SearchHit searchHit = mock(SearchHit.class);

        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");
        sourceAsMap.put("timestamp", "2023-06-16T12:22:15.189Z");
        sourceAsMap.put("date", "2023-06-16");

        when(searchResponse.getHits()).thenReturn(searchHits);

        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());

        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<LogEntity> logs = logService.projectByDynamic("source","message","timestamp","date");

        assertEquals(1, logs.size());
        assertEquals("source1", logs.get(0).getSource());
        assertEquals("message1", logs.get(0).getMessage());

    }



}