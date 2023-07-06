package com.example.LogAnalyzer.Helper;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryPrinter {


    public static String printQuery(SearchRequest searchRequest,RestHighLevelClient client) {


        ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {

            @Override
            public void onResponse(SearchResponse response) {
            }

            @Override
            public void onFailure(Exception e) {
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);

        String queryString = new String(searchRequest.source().toString().getBytes());
        System.out.println(queryString);
        return queryString;
    }
}
