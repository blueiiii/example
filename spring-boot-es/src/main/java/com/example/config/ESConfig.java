package com.example.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESConfig {

	 @Bean
    public RestHighLevelClient heightClient() {
		 RestHighLevelClient client = new RestHighLevelClient(
			        RestClient.builder(
			                new HttpHost("192.168.12.13", 9200, "http")));
		 return client;
	 }

}
