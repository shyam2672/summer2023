package com.example.LogAnalyzer.Service;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Helper.ExceltoEs;
import com.example.LogAnalyzer.Repository.LogRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
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

    public LogServiceImp(RestHighLevelClient client){
        this.client=client;
    }

    @Override
    public List<LogEntity> savelogdata() {
      return helper.WriteToEs(logRepository,helper.ReadFromExcel());
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
         int f=0;
         for(SearchHit hit: hits){
             Map<String,Object> sourceAsMap=hit.getSourceAsMap();
f++;
             String source= (String) sourceAsMap.get("source");
             String message= (String) sourceAsMap.get("message");
             LogEntity logg=new LogEntity();
             logg.setID(String.valueOf(f));
             logg.setSource(source);
             logg.setMessage(message);
             System.out.println(source+ "----" + message);
logs.add(logg);
         }

///gfgeg
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

        int f=0;

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
f++;
        }
        System.out.println(f);
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

        int f=0;
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
            f++;
        }
        System.out.println(f);

        return logs;
    }


}
