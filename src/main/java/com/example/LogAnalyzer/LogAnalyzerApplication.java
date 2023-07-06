package com.example.LogAnalyzer;

import com.example.LogAnalyzer.Entity.LogEntity;
import com.example.LogAnalyzer.Repository.LogRepository;
import com.example.LogAnalyzer.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.text.ParseException;
import java.time.LocalDateTime;
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


  LogService obj=context.getBean(LogService.class);
//
//// List<LogEntity> f= obj.savelogdata();
//		String start = "2023-06-16T11:22:14";
//		String end = "2024-06-16T11:22:14";

//		obj.savelogdata();
////		 obj.filterBytime(LocalDateTime.parse(start),LocalDateTime.parse(end));
////obj.search();
////		try {
////			obj.filterByTermsDynamic("source","standalone-reporting-sch-slave-deployment-6d978d7d87-6fxv7","standalone-reporting-sch-slave-deployment-6d978d7d87-b9fvc");
////		} catch (ParseException e) {
////			throw new RuntimeException(e);
////		}
//		obj.projectByDynamic("source","message","date","_id");
//		obj.filterByterms();
//
//		obj.search();
//		System.out.println(obj.cardinalityAggs("date"));
////		System.out.println(obj.searchUsingScroll().size());
//		List<LogEntity> f=obj.searchUsingPage();
// for(LogEntity ff:f){
//	 System.out.println(ff.getTimestamp());
//	 System.out.println(ff.getMessage());
// }

//
// obj.search();



	}



}
