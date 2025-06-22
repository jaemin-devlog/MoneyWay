package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.GPTDayPlanDto;
import com.example.moneyway.ai.dto.response.GPTPlaceDto;
import com.example.moneyway.ai.support.PromptBuilder;
import com.example.moneyway.infrastructure.openai.OpenAiClient;
import com.example.moneyway.place.domain.TourPlace;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GPT 기반 여행 일정 추천 서비스 클래스.
 * 사용자 입력 조건 + 장소 후보 리스트를 기반으로 GPT에게 요청을 보내고,
 * 응답(JSON)을 파싱하여 하루 단위 일정 리스트로 반환한다.
 */
@Service
@RequiredArgsConstructor
public class AiPlannerService {

    // 🔗 프롬프트를 생성해주는 빌더 (사용자 조건 → GPT 프롬프트 문자열)
    private final PromptBuilder promptBuilder;

    // 🔗 실제 GPT API를 호출하는 컴포넌트 (GPT 호출 전용)
    private final OpenAiClient openAiClient;

    /**
     * GPT에게 여행 일정을 추천받고, 결과를 파싱하여 반환하는 핵심 메서드
     *
     * @param request    사용자 여행 조건 DTO
     * @param candidates 추천 후보 장소 리스트
     * @return 하루 단위 일정 추천 결과 리스트
     */
    public List<GPTDayPlanDto> getRecommendedPlan(TravelPlanRequestDto request, List<TourPlace> candidates) {
        // 1️⃣ 사용자 조건과 후보 장소를 기반으로 프롬프트 문자열 생성
        String prompt = promptBuilder.buildPrompt(request, candidates);

        // 2️⃣ 생성된 프롬프트를 GPT에 전달하고 응답 문자열(JSON)을 받아옴
        String response = openAiClient.call(prompt);

        // 3️⃣ 응답 문자열(JSON)을 파싱하여 하루 단위 일정 리스트로 변환
        return parseResponse(response);
    }

    /**
     * GPT로부터 받은 JSON 응답을 파싱하여 List<GPTDayPlanDto>로 변환하는 내부 메서드
     *
     * 예상 응답 예시:
     * {
     *   "Day 1": [
     *     { "place": "용두암", "type": "관광지", "time": "오전", "cost": 0 }
     *   ],
     *   "Day 2": [
     *     { "place": "성산일출봉", "type": "관광지", "time": "오전", "cost": 2000 }
     *   ]
     * }
     *
     * @param json GPT 응답 JSON 문자열
     * @return 하루 일정 DTO 리스트
     */
    private List<GPTDayPlanDto> parseResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper(); // Jackson JSON 파서 사용

            // GPT 응답은 Map<String, List<GPTPlaceDto>> 형태이므로 타입을 명시
            Map<String, List<GPTPlaceDto>> dayMap = mapper.readValue(
                    json, new TypeReference<>() {}
            );

            // Map → DTO 리스트로 변환 (예: "Day 1" → GPTDayPlanDto)
            return dayMap.entrySet().stream()
                    .map(entry -> new GPTDayPlanDto(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // JSON 파싱 실패 시 예외 발생 → 프론트에 에러 전파 가능
            throw new RuntimeException("GPT 응답 파싱 실패", e);
        }
    }
}
