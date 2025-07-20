package com.example.moneyway.ai.support;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.place.domain.Place;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PromptBuilder {

    private String promptTemplate;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/ai-planner-prompt.txt");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            this.promptTemplate = FileCopyUtils.copyToString(reader);
            log.info("AI Planner 프롬프트 템플릿 로딩 성공");
        } catch (IOException e) {
            log.error("AI Planner 프롬프트 템플릿 로딩 실패", e);
            throw new RuntimeException("AI Planner 프롬프트 템플릿 로딩 실패", e);
        }
    }

    public String buildPrompt(TravelPlanRequestDto request, List<Place> candidates) {
        String userConditions = buildUserConditions(request);
        String placeList = buildPlaceList(candidates);

        return promptTemplate.formatted(userConditions, placeList, request.getDuration(), request.getBudget());
    }

    private String buildUserConditions(TravelPlanRequestDto request) {
        StringBuilder sb = new StringBuilder();
        sb.append("- 여행 기간: ").append(request.getDuration()).append("일\n");
        sb.append("- 인원: ").append(request.getCompanion()).append("명\n");
        sb.append("- 예산: ").append(request.getBudget()).append("원\n");
        if (request.getThemes() != null && !request.getThemes().isEmpty()) {
            sb.append("- 선호 카테고리: ").append(String.join(", ", request.getThemes())).append("\n");
        }
        return sb.toString();
    }

    private String buildPlaceList(List<Place> candidates) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<Place>> grouped = candidates.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(p -> p.getCategory().getDisplayName()));

        List<String> categories = Arrays.asList("관광지", "맛집", "카페", "숙소", "액티비티/체험", "쇼핑");
        for (String category : categories) {
            List<Place> filtered = grouped.getOrDefault(category, List.of());
            if (!filtered.isEmpty()) {
                sb.append("[").append(category).append("]\n");
                filtered.stream()
                        .limit(10)
                        .forEach(place -> sb.append("- ")
                                .append(place.getPlaceName())
                                .append(" | type: ").append(place.getCategory().getDisplayName())
                                .append(" | cost: ").append(place.getNumericPrice())
                                .append("\n"));
            }
        }
        return sb.toString();
    }
}

