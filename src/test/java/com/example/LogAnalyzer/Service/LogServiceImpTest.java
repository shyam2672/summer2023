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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public
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
    public static void initlogs() {
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
    public void saveTest() {
        //stubbing the helper object as it is tested separately
        when(helper.ReadFromExcel()).thenReturn(logs);
        when(helper.WriteToEs(any(LogRepository.class), anyList())).thenReturn(logs);
        assertEquals(logs, logService.savelogdata());


    }

    @Test
    public void searchTest() {
        //stubbing the log repository
        when(logRepository.findAll()).thenReturn(logs);
        List<LogEntity> actualLogs = logService.search();
        assertEquals(logs, actualLogs);
    }


    @Test
    public void groupByTest() {

        // mocking the SearchResponse Class
        SearchResponse searchResponse = mock(SearchResponse.class);

        // mocking the Aggregations Class
        Aggregations aggs = mock(Aggregations.class);

        // mocking the Terms Class
        Terms sourceaggs = mock(Terms.class);

        // mocks of type Terms.Bucket and stubbing accordingly
        Terms.Bucket bucket1 = mock(Terms.Bucket.class);
        when(bucket1.getKeyAsString()).thenReturn("source1");
        when(bucket1.getDocCount()).thenReturn(10L);
        Terms.Bucket bucket2 = mock(Terms.Bucket.class);
        when(bucket2.getKeyAsString()).thenReturn("source2");
        when(bucket2.getDocCount()).thenReturn(20L);


        List<Terms.Bucket> terms = new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);


        //return list of mocked buckets
        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();

        // return sourceaggs of type Terms when requested
        when(aggs.get("sources")).thenReturn(sourceaggs);

        // return our mocked searchResponse when requested
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //return our mocked aggregation: aggs
        when(searchResponse.getAggregations()).thenReturn(aggs);


        // calling the method under test
        Map<String, Long> result = logService.groupBysource();


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(searchResponse).getAggregations();

        //assertions
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(10L), result.get("source1"));
        assertEquals(Long.valueOf(20L), result.get("source2"));

    }

    @Test
    public void ProjectionTest() {

        // Creating mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);

        // data would be in map format
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");

        // stubbing the mock behaviour
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());
        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // calling the method under test
        List<LogEntity> logs = logService.projectBySourceAndMessage();


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();

        //Assertions
        assertEquals(1, logs.size());
        assertEquals("source1", logs.get(0).getSource());
        assertEquals("message1", logs.get(0).getMessage());

    }


    @Test
    public void filterByTimeTest() {

        //Creating Mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);


        String start = "2023-06-16T11:22:14";
        String end = "2024-06-16T11:22:14";

        // Data that should get returned
        Map<String, Object> sourceAsMap = new HashMap<>();
//        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");
        sourceAsMap.put("date", LocalDate.now());
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");

        //stubbing the mocks
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());
        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Call the method under test
        List<LogEntity> logs = logService.filterBytime(start, end);


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();


        //Assertions
        assertEquals(1, logs.size());
//        assertEquals("1", logs.get(0).getID());
    }

    @Test
    public void filterByTimeTestFail() {

        //Creating the mocks
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);

        //Data that should be returned
        String start = "2023-06-16T11:22:14";
        String end = "2023-06-16T11:23:14";
        String now = "2024-06-16T11:22:14";
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T12:22:15.189Z");
        sourceAsMap.put("date", LocalDate.now());
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");

        //stubbing the mcoks
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());
        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Call the method under test
        List<LogEntity> logs = logService.filterBytime(start, end);


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();


        Date ts = logs.get(0).getTimestamp();
        System.out.println(ts);
        Instant instant = ts.toInstant();

        // Convert Instant to LocalDateTime
        LocalDateTime tsp = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        boolean isAfter = tsp.isAfter(LocalDateTime.parse(start));
        boolean isBefore = tsp.isBefore(LocalDateTime.parse(end));

        //Assertions
        assertEquals(1, logs.size());
        assertFalse(isBefore && isAfter);

    }

    @Test
    public void filterByTermsTest() {
        // Creating mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);

        //Data that would be returned
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");
        sourceAsMap.put("date", LocalDate.now());
        sourceAsMap.put("source", "standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7");
        sourceAsMap.put("message", "message1");

        //stubbing the mocks
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());
        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //call the method under test
        List<LogEntity> logs = logService.filterByterms();


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();

        //Assertions
        assertEquals("standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7", logs.get(0).getSource());
    }

    @Test
    public void filterByTermsTestFail() {
        // Create Mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);

        // Data as Map
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("ID", "1");
        sourceAsMap.put("timestamp", "2023-06-16T17:52:14.189Z");
        sourceAsMap.put("date", LocalDate.now());
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");

        //stub the mcoks
        when(searchResponse.getHits()).thenReturn(searchHits);
        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());
        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //call the method under test
        List<LogEntity> logs = logService.filterByterms();

        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();


        //Assertions
        assertNotEquals("standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7", logs.get(0).getSource());
    }

    @Test
    public void testSearchUsingPage() {
        // creating pages
        Page<LogEntity> page1 = createLogEntityPage(1, 1000, true);
        Page<LogEntity> page2 = createLogEntityPage(1001, 2000, false);

        // stubbing findall function of logrepository to return desired pages
        when(logRepository.findAll(any(Pageable.class))).thenReturn(page1, page2);

        List<LogEntity> expectedLogs = new ArrayList<>();
        expectedLogs.addAll(page1.getContent());
        expectedLogs.addAll(page2.getContent());

        // Call the function under test
        List<LogEntity> actualLogs = logService.searchUsingPage();

        // verify
        verify(logRepository, times(2)).findAll(any(Pageable.class));

        //Assertions
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
    public void searchUsingScrollTest() {
        // Create Mock objects
        SearchResponse searchResponse1 = mock(SearchResponse.class);
        SearchResponse searchResponse2 = mock(SearchResponse.class);
        SearchHits searchHits1 = mock(SearchHits.class);
        SearchHits searchHits2 = mock(SearchHits.class);
        SearchHit searchHit1 = mock(SearchHit.class);
        SearchHit searchHit2 = mock(SearchHit.class);

        //data in form of map
        Map<String, Object> sourceAsMap1 = new HashMap<>();
        Map<String, Object> sourceAsMap2 = new HashMap<>();
        sourceAsMap1.put("source", "source1");
        sourceAsMap1.put("message", "message1");
        sourceAsMap1.put("timestamp", "2023-06-16T17:52:14.692Z");
        sourceAsMap1.put("date", "2023-06-16");

        sourceAsMap2.put("source", "source2");
        sourceAsMap2.put("message", "message2");
        sourceAsMap2.put("timestamp", "2023-06-16T17:52:14.692Z");
        sourceAsMap2.put("date", "2023-06-16");


        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(100);

        // stubbing the mocks
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
        when(searchHits1.getHits()).thenReturn(new SearchHit[]{searchHit1, searchHit2});
        when(searchHits2.getHits()).thenReturn(new SearchHit[]{});
        when(searchHits1.iterator()).thenReturn(List.of(searchHit1, searchHit2).iterator());
        when(searchHit1.getSourceAsMap()).thenReturn(sourceAsMap1);
        when(searchHit2.getSourceAsMap()).thenReturn(sourceAsMap2);


        // call the method under test
        List<LogEntity> logs = logService.searchUsingScroll();


        //expected response
        List<LogEntity> expectedLogs = new ArrayList<>();
        LogEntity log1 = new LogEntity();
        log1.setID("1");
        log1.setSource("source1");
        log1.setMessage("message1");
        expectedLogs.add(log1);
        LogEntity log2 = new LogEntity();
        log2.setID("2");
        log2.setSource("source2");
        log2.setMessage("message2");
        expectedLogs.add(log2);

        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse1).getHits();


        //Assertions
        assertEquals(expectedLogs.size(), logs.size());
        for (int i = 0; i < expectedLogs.size(); i++) {
            LogEntity logg1 = expectedLogs.get(i);
            LogEntity logg2 = logs.get(i);
            assertEquals(logg1.getMessage(), logg2.getMessage());
            assertEquals(logg1.getSource(), logg2.getSource());

        }

    }

    @Test
    public void tabularAggTest() {

        //Create Mocks
        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);
        Terms sourceaggs = mock(Terms.class);


        // mock data
        Terms.Bucket bucket1 = mock(Terms.Bucket.class);
        when(bucket1.getKeyAsString()).thenReturn("source1");
        Terms.Bucket bucket2 = mock(Terms.Bucket.class);
        when(bucket2.getKeyAsString()).thenReturn("source2");
        List<Terms.Bucket> terms = new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);

        Cardinality uniqueTimestamps = mock(Cardinality.class);


        //stubbing the mocks
        when(uniqueTimestamps.getValue()).thenReturn(100L);
        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();
        when(searchResponse.getAggregations()).thenReturn(aggs);
        when(aggs.get("timestamps_per_source")).thenReturn(sourceaggs);
        when(bucket1.getAggregations()).thenReturn(aggs);
        when(bucket2.getAggregations()).thenReturn(aggs);
        when(aggs.get("unique_timestamps")).thenReturn(uniqueTimestamps);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // call the method under test
        Map<String, Long> result = logService.tabularAggregation();
/// verify
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getAggregations();


        //Assertions
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(100L), result.get("source1"));
        assertEquals(Long.valueOf(100L), result.get("source2"));


    }

    @Test
    public void nestedAggTest() {

        // create mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        Aggregations aggs = mock(Aggregations.class);
        Terms sourcesAgg = mock(Terms.class);

        List<Terms.Bucket> sourcesBuckets = new ArrayList<>();
        Terms.Bucket sourcesBucket1 = mock(Terms.Bucket.class);

        when(sourcesBucket1.getKeyAsString()).thenReturn("source1");
        Terms.Bucket sourcesBucket2 = mock(Terms.Bucket.class);


        // stub the mock behaviour
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
        when(aggs.get("unique_dates")).thenReturn(uniqueIds);


        // Call the method under test
        Map<String, Long> mp = logService.nestedAggregation();


        //verify
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(sourcesAgg).getBuckets();
        verify(timestampsAgg, times(2)).getBuckets();


        //Assertions
        assertEquals(mp.get("source1-2019-01-01"), 100L);
        assertEquals(mp.get("source2-2019-01-02"), 100L);

    }

    @Test
    public void cardinalityaggTest() {

        // create mock objects
        SearchResponse searchResponse = mock(SearchResponse.class);
        Cardinality cardinalityAgg = mock(Cardinality.class);
        Aggregations aggs = mock(Aggregations.class);

        //stub the mocks
        when(cardinalityAgg.getValue()).thenReturn(100L);
        when(searchResponse.getAggregations()).thenReturn(aggs);
        when(aggs.get("unique_" + "field"))
                .thenReturn(cardinalityAgg);
        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // call the method under test
        long cardinality = logService.cardinalityAggs("field");


        //verify
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(cardinalityAgg).getValue();

        //Assertions
        assertEquals(100L, cardinality);
    }


    @Test
    public void testFilterByTermsDynamic() throws Exception {

        //expected data
        String field = "source";
        String[] terms = {"app-1", "app-2"};
        String timestamp = "2023-06-30T12:34:56.789Z";
        String source = "app-1";
        String message = "message";
        Map<String, Object> sourceAsMap = Map.of("timestamp", timestamp, "date", "2023-06-30", "source", source, "message", message);

        // create mock objects and stub them
        SearchHit hit = mock(SearchHit.class);
        when(hit.getSourceAsMap()).thenReturn(sourceAsMap);
        SearchHits hits = mock(SearchHits.class);
        when(hits.iterator()).thenReturn(List.of(hit).iterator());
        SearchResponse response = mock(SearchResponse.class);
        when(response.getHits()).thenReturn(hits);
        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(response);

        // call method under test
        List<LogEntity> logs = logService.filterByTermsDynamic(field, terms);

        // assertions
        assertEquals(1, logs.size());
        LogEntity log = logs.get(0);
        assertEquals(LocalDate.of(2023, 6, 30), log.getDate());
        assertEquals(source, log.getSource());
        assertEquals(message, log.getMessage());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setLenient(false);
        Date expectedTimestamp = formatter.parse(timestamp);
        assertEquals(expectedTimestamp, log.getTimestamp());

        //verify
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGroupByDynamic() throws IOException {

        //create mock objects  abd stub them
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
        List<Terms.Bucket> terms = new ArrayList<Terms.Bucket>();
        terms.add(bucket1);
        terms.add(bucket2);
        List<Terms.Bucket> buckets = List.of(bucket1, bucket2);
        doAnswer(invocation -> {
            return buckets;
        }).when(sourceaggs).getBuckets();
        when(aggs.get("groupBy_field")).thenReturn(sourceaggs);

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // call methodunder test
        Map<String, Long> result = logService.groupByDynamic("field");
//

        //verify
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(searchResponse).getAggregations();

        //Assertions
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(10L), result.get("field1"));
        assertEquals(Long.valueOf(20L), result.get("field2"));

    }


    @Test
    public void projectiondynamictest() {

        //create mcok objects  and stub them
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits searchHits = mock(SearchHits.class);
        SearchHit searchHit = mock(SearchHit.class);

        //data
        Map<String, Object> sourceAsMap = new HashMap<>();
        sourceAsMap.put("source", "source1");
        sourceAsMap.put("message", "message1");
        sourceAsMap.put("timestamp", "2023-06-16T12:22:15.189Z");
        sourceAsMap.put("date", "2023-06-16");

        when(searchResponse.getHits()).thenReturn(searchHits);

        when(searchHits.iterator()).thenReturn(List.of(searchHit).iterator());

        when(searchHit.getSourceAsMap()).thenReturn(sourceAsMap);
        when(searchHit.getId()).thenReturn("id");

        try {
            when(client.search(any(), any())).thenReturn(searchResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // call the method under  test
        List<Map<String, Object>> logs = logService.projectByDynamic("source", "message", "timestamp", "date");


        //verifying whether specific methods of our mocked objects were called
        try {
            verify(client).search((SearchRequest) any(), any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        verify(searchResponse).getHits();

        //Assertions
        assertEquals(1, logs.size());
        assertEquals("source1", logs.get(0).get("source"));
        assertEquals("message1", logs.get(0).get("message"));

    }


}