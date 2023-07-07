package com.example.LogAnalyzer.Configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigTest {

    @Test
    public void testClient() {
        Config config = new Config();
        RestHighLevelClient client = config.client();
        assertNotNull(client);
    }

}