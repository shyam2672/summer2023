//package com.example.LogAnalyzer.Configuration;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration;
//
//
//@Configuration
//public class Config  {
//
//    @Bean(destroyMethod = "close")
//    public RestHighLevelClient client() {
//        return new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http"))
//        );
//    }
//
//}
