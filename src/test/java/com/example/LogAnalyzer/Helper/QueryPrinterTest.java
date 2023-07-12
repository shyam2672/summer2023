package com.example.LogAnalyzer.Helper;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueryPrinterTest {

    @Test
    void printQuery() {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        SearchResponse searchResponse = mock(SearchResponse.class);
        SearchHits hits = mock(SearchHits.class);
        when(searchResponse.getHits()).thenReturn(hits);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        SearchRequest searchRequest = new SearchRequest("my_index");
        searchRequest.source(sourceBuilder);

        ActionListener<SearchResponse> listener = Mockito.mock(ActionListener.class);
        RequestOptions requestOptions = RequestOptions.DEFAULT.toBuilder().build();
        doAnswer(invocation -> {
            listener.onResponse(searchResponse);
            return null;
        }).when(client).searchAsync(eq(searchRequest), eq(requestOptions), eq(listener));

        String queryString = QueryPrinter.printQuery(searchRequest, client);
        String expectedQueryString = sourceBuilder.toString();
        assertEquals(expectedQueryString, queryString);
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(client).searchAsync(eq(searchRequest), eq(requestOptions), captor.capture());
        ActionListener listener1 = captor.getValue();
        verify(client).searchAsync(eq(searchRequest), eq(requestOptions), eq(listener1));
    }
}