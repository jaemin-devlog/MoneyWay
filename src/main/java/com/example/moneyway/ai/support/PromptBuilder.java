package com.example.moneyway.ai.support;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.place.domain.TourPlace;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 사용자의 입력 조건과 추천 장소 후보 리스트를 기반으로
 * GPT에게 전달할 프롬프트를 구성하는 클래스입니다.
 * GPT는 이 프롬프트를 바탕으로 예산 내 여행 일정을 구성합니다.
 */
@Component
public class PromptBuilder {

    public String buildPrompt(TravelPlanRequestDto request, List<TourPlace> candidates) {
        StringBuilder prompt = new StringBuilder();

        // 1. 사용자 조건 요약
        prompt.append("다음은 사용자의 여행 조건입니다:\n");
        prompt.append("- 지역: ").append(request.getRegion()).append("\n");
        prompt.append("- 여행 기간: ").append(request.getDuration()).append("일\n");
        prompt.append("- 인원: ").append(request.getCompanion()).append("\n");
        prompt.append("- 예산: ").append(request.getBudget()).append("원\n");
        prompt.append("- 교통: ").append(request.getTransport()).append("\n");
        if (request.getThemes() != null && !request.getThemes().isEmpty()) {
            prompt.append("- 스타일: ").append(String.join(", ", request.getThemes())).append("\n");
        }

        // 2. 장소 리스트 (카테고리별 요약, 최대 3개씩)
        prompt.append("\n사용자 조건에 맞는 장소 후보 목록입니다.\n");
        Map<String, List<TourPlace>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(TourPlace::getCat1));

        List<String> categories = Arrays.asList("관광지", "식사", "숙소");
        for (String category : categories) {
            List<TourPlace> filtered = grouped.getOrDefault(category, List.of()).stream()
                    .limit(3)
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                prompt.append("[").append(category).append("]\n");
                for (TourPlace place : filtered) {
                    int price = place.getPrice(); // 💡 변경됨
                    prompt.append("- ").append(place.getTitle());
                    prompt.append(" (").append(price).append("원)\n");
                }
            }
        }

        // 3. 일정 생성 요청
        prompt.append("\n위 장소 중 일부를 사용해서 ");
        prompt.append(request.getDuration()).append("일간 여행 일정을 구성해주세요.\n");
        prompt.append("예산 ").append(request.getBudget()).append("원을 초과하지 않게 하고,\n");
        prompt.append("오전, 점심, 오후, 저녁, 숙소 시간대로 구분해 주세요.\n");

        // 4. 출력 예시
        prompt.append("가능하다면 아래와 같은 JSON 형식으로 응답해 주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"Day 1\": [\n");
        prompt.append("    { \"place\": \"용두암\", \"type\": \"관광지\", \"time\": \"오전\", \"cost\": 0 },\n");
        prompt.append("    { \"place\": \"흑돼지거리\", \"type\": \"식사\", \"time\": \"점심\", \"cost\": 15000 },\n");
        prompt.append("    { \"place\": \"메종글래드 제주\", \"type\": \"숙소\", \"time\": \"저녁\", \"cost\": 70000 }\n");
        prompt.append("  ]\n");
        prompt.append("}");

        return prompt.toString();
    }
}
