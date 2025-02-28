package com.moura.consumer.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {
    @Bean
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(1000);
        clientHttpRequestFactory.setReadTimeout(1000);

        return new RestTemplate(clientHttpRequestFactory);
    }
}