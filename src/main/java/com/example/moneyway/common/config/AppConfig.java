package com.example.moneyway.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 전체 프로젝트에서 사용할 공통 Bean 등록
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8의 날짜/시간 타입을 직렬화/역직렬화하기 위해 필수적인 모듈
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
