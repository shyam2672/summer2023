package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Helper.QueryPrinter;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LogServiceImp implements LogService {


//    @Autowired
    private RestHighLevelClient client;
    private  LogRepository logRepository;

    private  ExceltoEs helper;
    @Autowired
    public LogServiceImp(ExceltoEs helper,LogRepository logRepository,RestHighLevelClient client){
        this.helper=helper;
        this.logRepository=logRepository;
        this.client=client;
    }


    @Override
    public List<LogEntity> savelogdata() {
        try{
            List<LogEntity> logs;
            logs= helper.WriteToEs(logRepository,helper.ReadFromExcel());
            return logs;

        }
        catch (Exception e){
throw new RuntimeException(e);
        }
    }

    @Override
    public List<LogEntity> search() {


        Iterable<LogEntity> logs = logRepository.findAll();
        List<LogEntity> loggs=new ArrayList<>();
        for (LogEntity log : logs) {
         loggs.add(log);
        }
        return loggs;
    }



    @Override
    public List<LogEntity> searchUsingPage() {
        Page<LogEntity> page = logRepository.findAll(Pageable.ofSize(1000));
        List<LogEntity> logs=new ArrayList<>();
        System.out.println(page.hasNext());
        while(page.hasNext()){
            logs.addAll(page.getContent());
            System.out.println(page.getContent().size());
            page=logRepository.findAll(page.nextPageable());
        }
        logs.addAll(page.getContent());
       page.nextPageable();
        for(LogEntity logg:logs){
            System.out.println(logg.getID());
        }
        System.out.println(logs.size());
        return logs;

    }

    @Override
    public List<LogEntity> searchUsingScroll() {
        Scroll scroll=new Scroll(TimeValue.timeValueMinutes(1L));

        SearchRequest searchRequest=new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(100);
        searchRequest.source(searchSourceBuilder);

        String scrollId = null;
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scrollId = searchResponse.getScrollId();
        List<LogEntity> logs=new ArrayList<>();
        int tothits=0;

        while (scrollId != null) {

            SearchHits hits= searchResponse.getHits();
//              hits.iterator();
            if(hits.getHits().length==0)break;
            for(SearchHit hit: hits){
                Map<String,Object> sourceAsMap=hit.getSourceAsMap();
//f++;
                tothits++;
                String source= (String) sourceAsMap.get("source");
                String message= (String) sourceAsMap.get("message");
                LogEntity logg=new LogEntity();
                logg.setID(String.valueOf(tothits));
                logg.setSource(source);
                logg.setMessage(message);
                System.out.println(source+ "----" + message);
                logs.add(logg);
            }


            try {
                searchResponse = client.scroll(new SearchScrollRequest(scrollId).scroll(scroll), RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scrollId = searchResponse.getScrollId();
        }
        return logs;
    }

    @Override
    public Map<String, Long> tabularAggregation() {
        QueryBuilder query=QueryBuilders.matchAllQuery();
        AggregationBuilder aggregation = AggregationBuilders
                .terms("timestamps_per_source").field("source").subAggregation(AggregationBuilders.cardinality("unique_timestamps").field("timestamp"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(new SearchSourceBuilder().query(query).aggregation(aggregation));
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println(searchResponse==null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Aggregations aggs=searchResponse.getAggregations();
        Terms sourceaggs=aggs.get("timestamps_per_source");

        List<? extends Terms.Bucket> sourceBuckets=sourceaggs.getBuckets();
        Map<String,Long> mp=new HashMap<>();
        for(Terms.Bucket sourcebucket:sourceBuckets){
            String source = sourcebucket.getKeyAsString();

            Cardinality uniqueTimestamps = sourcebucket.getAggregations().get("unique_timestamps");
            long value = uniqueTimestamps.getValue();
            mp.put(source,value);
            System.out.println("Source: " + source + ", Total Timestamps: " + value);
        }
        return mp;
    }

    @Override
    public Map<String, Long> nestedAggregation() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        TermsAggregationBuilder sourcesAggregation = AggregationBuilders.terms("sources").field("source");
        DateHistogramAggregationBuilder timestampsAggregation = AggregationBuilders.dateHistogram("timestamps").field("timestamp").calendarInterval(DateHistogramInterval.HOUR);
        CardinalityAggregationBuilder uniqueIdsAggregation = AggregationBuilders.cardinality("unique_ids").field("id");
        sourcesAggregation.subAggregation(timestampsAggregation.subAggregation(uniqueIdsAggregation));
        searchSourceBuilder.aggregation(sourcesAggregation);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Aggregations aggs = searchResponse.getAggregations();

        Terms sourcesAgg = aggs.get("sources");
        Map<String,Long> mp=new HashMap<>();

        for (Terms.Bucket sourcesBucket : sourcesAgg.getBuckets()) {
            String source = sourcesBucket.getKeyAsString();

            Histogram timestampsAgg = sourcesBucket.getAggregations().get("timestamps");



            for (Histogram.Bucket timestampsBucket : timestampsAgg.getBuckets()) {
                String timestamp = timestampsBucket.getKeyAsString();

                Cardinality uniqueIds = timestampsBucket.getAggregations().get("unique_ids");
                long value = uniqueIds.getValue();
                System.out.println("Source: " + source + ", Timestamp: " + timestamp + ", Unique IDs: " + value);
                mp.put(source+"-"+timestamp,value);
            }
        }
        return mp;

    }

    @Override
    public Long cardinalityAggs(String field) {
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .cardinality("unique_" + field) //agg name
                .field(field);

        SearchRequest searchRequest = new SearchRequest("loganalyzer");
        searchRequest.source(new SearchSourceBuilder()
                .aggregation(aggregationBuilder));
QueryPrinter.printQuery(searchRequest,client);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Cardinality cardinalityAgg = searchResponse.getAggregations().get("unique_" + field);
        long cardinality = cardinalityAgg.getValue();

        System.out.println("Cardinality (number of distinct values) for field " + field + " is: " + cardinality);
        return cardinality;
    }


    @Override
    public Map<String, Long> groupBysource() {
        System.out.println(client);
        System.out.println(logRepository.findAll());
        QueryBuilder query = QueryBuilders.matchAllQuery();

        AggregationBuilder aggregation = AggregationBuilders
                .terms("sources").field("source");


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(new SearchSourceBuilder().query(query).aggregation(aggregation));

        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println(searchResponse==null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Aggregations aggs=searchResponse.getAggregations();
        Terms sourceaggs=aggs.get("sources");

        List<? extends Terms.Bucket> sourceBuckets=sourceaggs.getBuckets();
   Map<String,Long> mp=new HashMap<>();
        for(Terms.Bucket sourceBucket:sourceBuckets){
            System.out.println("fff");
            System.out.println(sourceBucket.getKeyAsString()+ "---" + sourceBucket.getDocCount());
            mp.put(sourceBucket.getKeyAsString(),sourceBucket.getDocCount());
        }
//        System.out.println(aggs);
return mp;
    }

    @Override
    public List<LogEntity> projectBySourceAndMessage() {
        QueryBuilder query = QueryBuilders.matchAllQuery();

        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        searchSource.query(query);
        searchSource.from(0);
        searchSource.size(1000);

        String[] includes = { "source", "message" };
        String[] excludes = null;
        searchSource.fetchSource(includes, excludes);

        SearchRequest searchRequest = new SearchRequest("loganalyzer");
        searchRequest.source(searchSource);

        SearchResponse response;
        try {
            response = client.search(searchRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response);

         SearchHits hits= response.getHits();

         List<LogEntity> logs=new ArrayList<>();
         int tothits=0;
         for(SearchHit hit: hits){
             Map<String,Object> sourceAsMap=hit.getSourceAsMap();
//f++;
             tothits++;
             String source= (String) sourceAsMap.get("source");
             String message= (String) sourceAsMap.get("message");
             LogEntity logg=new LogEntity();
             logg.setID(String.valueOf(tothits));
             logg.setSource(source);
             logg.setMessage(message);
             System.out.println(source+ "----" + message);
logs.add(logg);
         }

        return logs;
    }

    @Override
    public List<LogEntity> filterBytime(LocalDateTime start, LocalDateTime end) {
        RangeQueryBuilder query = new RangeQueryBuilder("timestamp")
                .gte(start)
                .lte(end);

        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        searchSource.query(query);
        searchSource.from(0);
        searchSource.size(4000);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(searchSource);

        SearchResponse response;
        try {
            response = client.search(searchRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHits hits = response.getHits();

//        int f=0;

        List<LogEntity> logs=new ArrayList<>();

        for(SearchHit hit: hits){
            Map<String,Object> sourceAsMap=hit.getSourceAsMap();
            String id=(String) sourceAsMap.get("ID");
            LocalDateTime timestamp= (LocalDateTime) sourceAsMap.get("timestamp");
            String source= (String) sourceAsMap.get("source");
            String message= (String) sourceAsMap.get("message");
            LogEntity logg=new LogEntity();
            logg.setID(id);
            logg.setTimestamp(timestamp);
            logg.setSource(source);
            logg.setMessage(message);
            logs.add(logg);
            System.out.println(source+ "----" + message);
//f++;
        }
//        System.out.println(f);
        return logs;
    }

    @Override
    public List<LogEntity> filterByterms() {
        TermsQueryBuilder termsfilter=QueryBuilders.termsQuery("source","standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7","standalone-reporting-sch-slave-deployment-6d978d7d87-b9fvc");

        QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(termsfilter);


        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        searchSource.query(query);
        searchSource.from(0);
        searchSource.size(4000);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(searchSource);

        SearchResponse response;
        try {
            response = client.search(searchRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHits hits = response.getHits();

//        int f=0;
        List<LogEntity> logs=new ArrayList<>();

        for(SearchHit hit: hits){
            Map<String,Object> sourceAsMap=hit.getSourceAsMap();
            String id=(String) sourceAsMap.get("ID");
            LocalDateTime timestamp= (LocalDateTime) sourceAsMap.get("timestamp");
            String source= (String) sourceAsMap.get("source");
            String message= (String) sourceAsMap.get("message");
            LogEntity logg=new LogEntity();
            logg.setID(id);
            logg.setTimestamp(timestamp);
            logg.setSource(source);
            logg.setMessage(message);
            logs.add(logg);
            System.out.println(source+ "----" + message);
//            f++;
        }
//        System.out.println(f);

        return logs;
    }






}
