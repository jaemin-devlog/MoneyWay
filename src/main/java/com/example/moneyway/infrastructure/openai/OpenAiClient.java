package com.example.moneyway.infrastructure.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * OpenAI GPT API 호출을 담당하는 컴포넌트.
 * 프롬프트를 전달하고, 문자열 응답(JSON)을 받아서 반환한다.
 */
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    // 사용할 GPT 모델과 OpenAI API 엔드포인트
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    /**
     * 사용자 프롬프트를 GPT에 전달하고 응답 문자열을 반환한다.
     *
     * @param prompt 사용자 입력을 기반으로 생성한 GPT 프롬프트
     * @return GPT가 생성한 텍스트 응답 (JSON 포맷 일정 추천)
     */
    public String call(String prompt) {
        // 1. 메시지 구성
        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        // 2. 요청 바디 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.7);
        requestBody.put("messages", List.of(userMessage));

        // 3. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. OpenAI API 호출
        ResponseEntity<Map> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 5. 응답에서 content 추출
        Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) ((List<?>) response.getBody().get("choices")).get(0)).get("message");
        return message.get("content").toString().trim();
    }
}
