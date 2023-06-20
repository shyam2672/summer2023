package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Configuration.Config;
import com.example.LogAnalyzer.Configuration.Entity.LogEntity;
import com.example.LogAnalyzer.Configuration.Helper.ExceltoEs;
import com.example.LogAnalyzer.Configuration.Repository.LogRepository;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.List;

//import static com.example.LogAnalyzer.Configuration.Helper.ExceltoEs.ReadFromExcel;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.LogAnalyzer")
public class LogAnalyzerApplication {

//@Autowired
//static RestHighLevelClient client;
//   static RestHighLevelClient client=new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));

	public static void main(String[] args) {
		ApplicationContext context=SpringApplication.run(LogAnalyzerApplication.class, args);
//       AggDemo();
//		ReadFromExcel();
//		ReadFromExcel();ReadFromExcel

  ExceltoEs obj=context.getBean(ExceltoEs.class);

  obj.WriteToEs(obj.ReadFromExcel());




	}



//	public static void AggDemo(){
//		SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder().
//				size(0).
//				query(QueryBuilders.matchAllQuery()).aggregation(AggregationBuilders.terms("continents").field("geoip.continent_name").
//						subAggregation(AggregationBuilders.terms("cities").field("geoip.city_name")));
//
//		SearchRequest request=new SearchRequest().indices("kibana_sample_data_ecommerce").source(searchSourceBuilder);
//
//
//		try {
//			SearchResponse response=client.search(request,
//					RequestOptions.DEFAULT);
//
////			System.out.println(response);
//
//			Aggregations aggs=response.getAggregations();
//
//			Terms continentaggs=aggs.get("continents");
//
//			List<? extends Terms.Bucket> continentBuckets=continentaggs.getBuckets();
//
//			for(Terms.Bucket continentBucket: continentBuckets){
//				System.out.println("f");
//				System.out.println(continentBucket.getKeyAsString() + "---" + continentBucket.getDocCount());
//				aggs =continentBucket.getAggregations();
//
//				Terms citiesAggs=aggs.get("cities");
//
//				List<? extends Terms.Bucket> cityBuckets=citiesAggs.getBuckets();
//
//				for(Terms.Bucket cityBucket:cityBuckets){
//					System.out.println(cityBucket.getKeyAsString() + "---" + cityBucket.getDocCount());
//				}
//
//
//			}
//
//
//
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//
//	}

}
