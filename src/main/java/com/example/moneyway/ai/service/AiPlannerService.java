package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.GPTDayPlanDto;
import com.example.moneyway.ai.dto.response.GPTPlaceDto;
import com.example.moneyway.ai.support.PromptBuilder;
import com.example.moneyway.common.exception.CustomException.CustomGptException;
import com.example.moneyway.infrastructure.openai.OpenAiClient;
import com.example.moneyway.place.domain.TourPlace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPlannerService {

    private final PromptBuilder promptBuilder;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public List<GPTDayPlanDto> getRecommendedPlan(TravelPlanRequestDto request, List<TourPlace> candidates) {
        String prompt = promptBuilder.buildPrompt(request, candidates);
        log.info("지역 {}에 대한 프롬프트 생성", request.getRegion());

        String response = openAiClient.call(prompt);
        log.info("GPT 응답 수신 완료");

        return parseResponse(response);
    }

    private List<GPTDayPlanDto> parseResponse(String response) {
        try {
            // 응답에서 첫 '{'부터 마지막 '}'까지 JSON 부분만 추출
            String jsonResponse = extractJson(response);

            Map<String, List<GPTPlaceDto>> dayMap = objectMapper.readValue(
                    jsonResponse, new TypeReference<>() {}
            );

            // 새로운 record 생성자 사용
            return dayMap.entrySet().stream()
                    .map(entry -> new GPTDayPlanDto(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            log.error("GPT 응답 JSON 파싱 실패. 응답: {}", response, e);
            // 새로운 커스텀 예외 사용
            throw new CustomGptException("GPT 응답을 파싱하는 데 실패했습니다.", e);
        }
    }

    /**
     * 더 큰 문자열에서 JSON 객체 문자열을 추출합니다.
     * GPT가 서두에 텍스트를 추가하는 경우를 처리합니다.
     */
    private String extractJson(String str) {
        int firstBrace = str.indexOf('{');
        int lastBrace = str.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return str.substring(firstBrace, lastBrace + 1);
        }
        // 유효한 JSON 객체를 찾지 못하면, 파서가 처리하도록 원본 문자열을 반환합니다.
        return str;
    }
}