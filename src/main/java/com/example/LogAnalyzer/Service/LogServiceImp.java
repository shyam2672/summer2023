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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class LogServiceImp implements LogService {


    private RestHighLevelClient client;
    private LogRepository logRepository;

    private ExceltoEs helper;

    //dependency injection using construction
    @Autowired
    public LogServiceImp(ExceltoEs helper, LogRepository logRepository, RestHighLevelClient client) {
        this.helper = helper;
        this.logRepository = logRepository;
        this.client = client;
    }


    @Override
    public List<LogEntity> savelogdata() {
        try {
            List<LogEntity> logs;
            logs = helper.ReadFromExcel();

            return helper.WriteToEs(logRepository, logs);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //simple search using elasticsearchRepository
    @Override
    public List<LogEntity> search() {


        Iterable<LogEntity> logs = logRepository.findAll();
        List<LogEntity> loggs = new ArrayList<>();
        for (LogEntity log : logs) {
//            System.out.println(log.getID());
            loggs.add(log);
        }
        return loggs;
    }

    //search using paging, good for small result set
    @Override
    public List<LogEntity> searchUsingPage() {
        Page<LogEntity> page = logRepository.findAll(Pageable.ofSize(1000));
        List<LogEntity> logs = new ArrayList<>();
        while (page.hasNext()) {
            logs.addAll(page.getContent());
            page = logRepository.findAll(page.nextPageable());
        }
        logs.addAll(page.getContent());
        page.nextPageable();
//        System.out.println(logs.size());
        return logs;

    }

    //seach using scroll, good for large result sets
    @Override
    public List<LogEntity> searchUsingScroll() {
        //data will remain in memory for 1 minute
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

        SearchRequest searchRequest = new SearchRequest();
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
        List<LogEntity> logs = new ArrayList<>();
        int tothits = 0;

        while (scrollId != null) {

            SearchHits hits = searchResponse.getHits();
            if (hits.getHits().length == 0) break;
            for (SearchHit hit : hits) {
                String id=hit.getId();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                System.out.println(sourceAsMap.toString());
                tothits++;
                LogEntity logg = new LogEntity();

                String timestamp = sourceAsMap.get("timestamp").toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                formatter.setLenient(false);
                Date tsp;
                try {
                    tsp = formatter.parse(timestamp);
                    logg.setTimestamp(tsp);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                logg.setTimestamp(tsp);
                LocalDate dt = LocalDate.parse(sourceAsMap.get("date").toString());
                logg.setDate(dt);
                String source = (String) sourceAsMap.get("source");
                String message = (String) sourceAsMap.get("message");
                logg.setSource(source);
                logg.setID(id);
                logg.setMessage(message);
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

    //an example of tabular aggregation,count of timestamps under a source
    @Override
    public Map<String, Long> tabularAggregation() {
        QueryBuilder query = QueryBuilders.matchAllQuery();
        AggregationBuilder aggregation = AggregationBuilders
                .terms("timestamps_per_source").field("source").subAggregation(AggregationBuilders.cardinality("unique_timestamps").field("timestamp"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(new SearchSourceBuilder().query(query).aggregation(aggregation));
        System.out.println(QueryPrinter.printQuery(searchRequest,client));
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println(searchResponse == null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Aggregations aggs = searchResponse.getAggregations();
        Terms sourceaggs = aggs.get("timestamps_per_source");

        List<? extends Terms.Bucket> sourceBuckets = sourceaggs.getBuckets();
        Map<String, Long> mp = new HashMap<>();
        for (Terms.Bucket sourcebucket : sourceBuckets) {
            String source = sourcebucket.getKeyAsString();

            Cardinality uniqueTimestamps = sourcebucket.getAggregations().get("unique_timestamps");
            long value = uniqueTimestamps.getValue();
            mp.put(source, value);
            System.out.println("Source: " + source + ", Total Timestamps: " + value);
        }
        System.out.println(mp.size());
        return mp;
    }
    //an example of nested aggregation,count of doc under a timestamp under a source
    @Override
    public Map<String, Long> nestedAggregation() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        TermsAggregationBuilder sourcesAggregation = AggregationBuilders.terms("sources").field("source");
        DateHistogramAggregationBuilder timestampsAggregation = AggregationBuilders.dateHistogram("timestamps").field("timestamp").calendarInterval(DateHistogramInterval.HOUR);
        CardinalityAggregationBuilder uniqueIdsAggregation = AggregationBuilders.cardinality("unique_dates").field("date");
        sourcesAggregation.subAggregation(timestampsAggregation.subAggregation(uniqueIdsAggregation));
        searchSourceBuilder.aggregation(sourcesAggregation);
        searchRequest.source(searchSourceBuilder);
        System.out.println(QueryPrinter.printQuery(searchRequest,client));
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Aggregations aggs = searchResponse.getAggregations();

        Terms sourcesAgg = aggs.get("sources");
        Map<String, Long> mp = new HashMap<>();

        for (Terms.Bucket sourcesBucket : sourcesAgg.getBuckets()) {
            String source = sourcesBucket.getKeyAsString();

            Histogram timestampsAgg = sourcesBucket.getAggregations().get("timestamps");


            for (Histogram.Bucket timestampsBucket : timestampsAgg.getBuckets()) {
                String timestamp = timestampsBucket.getKeyAsString();

                Cardinality uniqueIds = timestampsBucket.getAggregations().get("unique_dates");
                long value = uniqueIds.getValue();
                System.out.println("Source: " + source + ", Timestamp: " + timestamp + ", Unique Dates: " + value);
                mp.put(source + "-" + timestamp, value);
            }
        }
        return mp;

    }

    //can count cardinality of any field except messsage
    @Override
    public Long cardinalityAggs(String field) {
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .cardinality("unique_" + field) //agg name
                .field(field);

        SearchRequest searchRequest = new SearchRequest("loganalyzer");
        searchRequest.source(new SearchSourceBuilder()
                .aggregation(aggregationBuilder));
        QueryPrinter.printQuery(searchRequest, client);
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

    //ann example og grouby aggregation, tot docs under a source
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

            System.out.println(searchResponse == null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Aggregations aggs = searchResponse.getAggregations();
        Terms sourceaggs = aggs.get("sources");

        List<? extends Terms.Bucket> sourceBuckets = sourceaggs.getBuckets();
        Map<String, Long> mp = new HashMap<>();
        for (Terms.Bucket sourceBucket : sourceBuckets) {
            System.out.println("fff");
            System.out.println(sourceBucket.getKeyAsString() + "---" + sourceBucket.getDocCount());
            mp.put(sourceBucket.getKeyAsString(), sourceBucket.getDocCount());
        }
//        System.out.println(aggs);
        return mp;
    }

    // an example of porjection query
    @Override
    public List<LogEntity> projectBySourceAndMessage() {
        QueryBuilder query = QueryBuilders.matchAllQuery();

        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        searchSource.query(query);
        searchSource.from(0);
        searchSource.size(10000);

        String[] includes = {"source", "message"};
        String[] excludes = null;
        searchSource.fetchSource(includes, excludes);


        SearchRequest searchRequest = new SearchRequest("loganalyzer");
        searchRequest.source(searchSource);

        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response);

        SearchHits hits = response.getHits();

        List<LogEntity> logs = new ArrayList<>();
        int tothits = 0;
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//f++;
            tothits++;
            String source = (String) sourceAsMap.get("source");
            String message = (String) sourceAsMap.get("message");
            LogEntity logg = new LogEntity();
            logg.setID(String.valueOf(tothits));
            logg.setSource(source);
            logg.setMessage(message);
            System.out.println(source + "----" + message);
            logs.add(logg);
        }

        return logs;
    }

    //fitler docs in given time range
    @Override
    public List<LogEntity> filterBytime(String start, String end) {
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
        System.out.println(QueryPrinter.printQuery(searchRequest,client));
        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHits hits = response.getHits();


        List<LogEntity> logs = new ArrayList<>();

        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String id = (String) sourceAsMap.get("ID");
            LogEntity logg = new LogEntity();
            String timestamp = sourceAsMap.get("timestamp").toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setLenient(false);
            Date tsp;
            try {
                tsp = formatter.parse(timestamp);
                logg.setTimestamp(tsp);
            } catch (Exception e) {
                System.out.println(e);
            }

            LocalDate dt = LocalDate.parse(sourceAsMap.get("date").toString());
            String source = (String) sourceAsMap.get("source");
            String message = (String) sourceAsMap.get("message");
            logg.setID(id);

            logg.setSource(source);
            logg.setMessage(message);
            logg.setDate(dt);
            logs.add(logg);
            System.out.println(timestamp);
            System.out.println(source + "----" + message);
        }
        System.out.println(logs.size());
        return logs;
    }

    //fiters docs whose soucre belong to some specific options
    @Override
    public List<LogEntity> filterByterms() {
        TermsQueryBuilder termsfilter = QueryBuilders.termsQuery("source", "standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7", "standalone-reporting-sch-slave-deployment-6d978d7d87-b9fvc");

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
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SearchHits hits = response.getHits();
        List<LogEntity> logs = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap.toString());
            String id = (String) sourceAsMap.get("ID");
            LogEntity logg = new LogEntity();
            String timestamp = sourceAsMap.get("timestamp").toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setLenient(false);
            Date tsp;
            try {
                tsp = formatter.parse(timestamp);
                logg.setTimestamp(tsp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            LocalDate dt = LocalDate.parse(sourceAsMap.get("date").toString());
            String source = (String) sourceAsMap.get("source");
            String message = (String) sourceAsMap.get("message");
            logg.setID(id);

            logg.setSource(source);
            logg.setMessage(message);
            logg.setDate(dt);
            logs.add(logg);
            System.out.println(timestamp);
            System.out.println(source + "----" + message);
        }

        return logs;
    }
    //generic filter function wth any fied and any number of terms
    @Override
    public List<LogEntity> filterByTermsDynamic(String field, String... terms) throws ParseException {

        if (!field.equals("id") &&  !field.equals("timestamp") &&  !field.equals("source") && !field.equals("message")) {
            throw new RuntimeException("invalid field name");
        }
        for(String s:terms) System.out.println(s);
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(field, terms);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(termsQuery);

        sourceBuilder.size(10000);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(sourceBuilder);
        QueryPrinter.printQuery(searchRequest, client);

        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHits hits = response.getHits();

        List<LogEntity> logs = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String id = (String) sourceAsMap.get("ID");
            LogEntity logg = new LogEntity();
            String timestamp = sourceAsMap.get("timestamp").toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setLenient(false);
            Date tsp;
            try {
                tsp = formatter.parse(timestamp);
                logg.setTimestamp(tsp);
            } catch (Exception e) {
                throw e;
            }
            LocalDate dt = LocalDate.parse(sourceAsMap.get("date").toString());
            String source = (String) sourceAsMap.get("source");
            String message = (String) sourceAsMap.get("message");
            logg.setID(id);
            logg.setSource(source);
            logg.setMessage(message);
            logg.setDate(dt);
            logs.add(logg);
            System.out.println(timestamp);
            System.out.println(source + "----" + message);
        }
        System.out.println(logs.size());
        return logs;
    }
    //groupBys on given field
    @Override
    public Map<String, Long> groupByDynamic(String field) {

        QueryBuilder query = QueryBuilders.matchAllQuery();

        AggregationBuilder aggregation = AggregationBuilders
                .terms("groupBy_" + field).field(field).size(4000);


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("loganalyzer");
        searchRequest.source(new SearchSourceBuilder().query(query).aggregation(aggregation));
        QueryPrinter.printQuery(searchRequest, client);

        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println(searchResponse == null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Aggregations aggs = searchResponse.getAggregations();
        Terms fieldaggs = aggs.get("groupBy_" + field);
        int tot = 0;
        List<? extends Terms.Bucket> sourceBuckets = fieldaggs.getBuckets();
        Map<String, Long> mp = new HashMap<>();
        for (Terms.Bucket sourceBucket : sourceBuckets) {
            System.out.println("fff");
            System.out.println(sourceBucket.getKeyAsString() + "---" + sourceBucket.getDocCount());
            tot += sourceBucket.getDocCount();
            mp.put(sourceBucket.getKeyAsString(), sourceBucket.getDocCount());
        }
        System.out.println(tot);
        return mp;
    }

    //projects only the fields specified
    @Override
    public List<LogEntity> projectByDynamic(String... fields) {
        QueryBuilder query = QueryBuilders.matchAllQuery();

        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        searchSource.query(query);
        searchSource.from(0);
        searchSource.size(10000);

        String[] includes = fields;
        String[] excludes = null;
        searchSource.fetchSource(includes, excludes);

        SearchRequest searchRequest = new SearchRequest("loganalyzer");
        searchRequest.source(searchSource);

        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        SearchHits hits = response.getHits();

        List<LogEntity> logs = new ArrayList<>();
        int tothits = 0;
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            LogEntity logg = new LogEntity();
            System.out.println(sourceAsMap.toString());
            tothits++;
            String source, message, id;
            if (sourceAsMap.get("source") != null) {
                source = (String) sourceAsMap.get("source");
                logg.setSource(source);

            }
            if (sourceAsMap.get("message") != null) {
                message = (String) sourceAsMap.get("message");
                logg.setMessage(message);
            }
            if (sourceAsMap.get("date") != null) {
                LocalDate dt = LocalDate.parse(sourceAsMap.get("date").toString());
                logg.setDate(dt);
            }
            if (sourceAsMap.get("timestamp") != null) {
                String timestamp = sourceAsMap.get("timestamp").toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                formatter.setLenient(false);
                Date tsp;
                try {
                    tsp = formatter.parse(timestamp);
                    logg.setTimestamp(tsp);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }

            }

            logs.add(logg);
        }
        System.out.println(
                logs.size()
        );
        return logs;
    }

}
