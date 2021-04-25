package ru.gafarov.Family.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
