package com.example.moneyway.global.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;

@Slf4j //Lombok이 제공하는 로그관련 어노테이션
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://kauth.kakao.com")  // Kakao 인증 서버 기본 도메인
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)//
                 //모든 요청에 기본적으로 Content-Type: application/x-www-form-urlencoded 헤더를 포함
                 //Kakao 토큰 요청 시 필수 조건
                .clientConnector(new ReactorClientHttpConnector(//WebClient 내부에 사용할 HTTP 커넥터(Netty 기반)를 설정
                        HttpClient.create()//새로운 Netty HTTP 클라이언트를 생성
                                .responseTimeout(Duration.ofSeconds(5))          // 응답을 5초 동안 기다리다가 오지 않으면 타임아웃 예외 발생
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 서버에 연결을 시도하는 최대 시간은 3초로 제한
                ))
                .filter(logRequest())  // 요청 로깅
                .filter(logResponse()) // 응답 로깅
                .build();
    }

    // 요청 로그 필터
    private ExchangeFilterFunction logRequest() {//요청 로그를 출력하는 필터를 정의하는 메서드 시작
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {//요청 처리 직전에 실행될 함수 등록
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());//로그 출력: HTTP 메서드와 URI를 출력
            return reactor.core.publisher.Mono.just(clientRequest);//요청 객체를 그대로 다음 필터로 넘김 (가공 없이 전달)
        });
    }

    // 응답 로그 필터
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {//응답을 처리한 직후에 실행될 함수 등록
            log.info("Response: {}", clientResponse.statusCode());//로그 출력: 응답 상태 코드 (200, 401 등)를 출력
            return reactor.core.publisher.Mono.just(clientResponse);//응답 객체를 그대로 다음 체인으로 넘김 (가공 없이 전달)
        });
    }
}

