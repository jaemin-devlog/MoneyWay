package com.example.moneyway.infrastructure.openai;

import com.example.moneyway.infrastructure.openai.dto.OpenAiResponse; // DTO 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    public String call(String prompt) {
        Map<String, Object> systemMessage = Map.of("role", "system", "content", "너는 제주 여행 전문가이자 AI 여행 플래너야. 사용자 조건에 맞춰 일정 추천을 JSON으로만 출력해.");
        Map<String, Object> userMessage = Map.of("role", "user", "content", prompt);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.7);
        requestBody.put("messages", List.of(systemMessage, userMessage));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 응답을 Map 대신 DTO로 받습니다.
        ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                entity,
                OpenAiResponse.class // 응답 타입을 DTO 클래스로 지정
        );

        // 복잡한 캐스팅 대신, Optional과 메서드 체이닝으로 안전하고 깔끔하게 content를 추출합니다.
        return Optional.ofNullable(response.getBody())
                .map(OpenAiResponse::choices)
                .filter(choices -> !choices.isEmpty())
                .map(choices -> choices.get(0).message().content())
                .map(String::trim)
                .orElseThrow(() -> new RuntimeException("Failed to get a valid response from OpenAI."));
    }
}