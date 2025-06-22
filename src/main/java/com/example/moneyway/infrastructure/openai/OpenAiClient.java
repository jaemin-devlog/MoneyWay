package com.example.moneyway.infrastructure.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class OpenAiClient {

    @Value("${openai.key}")    // application.yml 또는 .env에서 openai.key 값을 주입받음
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();    // HTTP 요청을 보낼 때 사용할 RestTemplate 인스턴스

    public String getCompletion(String prompt) {    // GPT-3.5 모델에게 프롬프트를 보내고 응답 결과(문장)를 받아오는 메서드
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); //JSON형식으로 전송
        headers.setBearerAuth(apiKey); // Authorization: Bearer [API Key] 설정

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");// 사용할 GPT 모델 지정
        requestBody.put("temperature", 0.7);      // 창의성 정도 (0.0~1.0)

        List<Map<String, String>> messages = new ArrayList<>(); // messages 리스트: system 메시지 + user 메시지 구성
        messages.add(Map.of("role", "system", "content", "당신은 여행 일정 도우미입니다."));// GPT에게 역할을 부여함
        messages.add(Map.of("role", "user", "content", prompt));// 실제 사용자 요청 내용
        requestBody.put("messages", messages);// 최종 요청 바디에 messages 추가

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                Map.class
        );

        Map<String, Object> message = (Map<String, Object>) ((Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0)).get("message");
        return (String) message.get("content");
    }
}

