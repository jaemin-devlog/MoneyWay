package com.example.moneyway.ai.support;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.place.domain.TourPlace;
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

    // ✅ [수정] 프롬프트 템플릿을 저장할 클래스 멤버 변수를 선언합니다.
    private String promptTemplate;

    /**
     * ✅ [수정] @PostConstruct는 클래스의 다른 메서드와 독립적으로 위치해야 합니다.
     * 이 메서드는 PromptBuilder Bean이 생성된 직후, 프롬프트 파일을 미리 로드하는 역할을 합니다.
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/ai-planner-prompt.txt");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            this.promptTemplate = FileCopyUtils.copyToString(reader);
            log.info("AI Planner 프롬프트 템플릿 로딩 성공");
        } catch (IOException e) {
            log.error("AI Planner 프롬프트 템플릿 로딩 실패", e);
            // 프롬프트가 필수적이라면, 애플리케이션 시작을 막을 수도 있습니다.
            throw new RuntimeException("AI Planner 프롬프트 템플릿 로딩 실패", e);
        }
    }

    /**
     * 사용자 요청과 장소 후보를 기반으로 실제 GPT에 전달할 프롬프트를 생성합니다.
     */
    public String buildPrompt(TravelPlanRequestDto request, List<TourPlace> candidates) {
        String userConditions = buildUserConditions(request);
        String placeList = buildPlaceList(candidates);

        // ✅ [수정] init()에서 로드한 promptTemplate 필드를 사용합니다.
        return promptTemplate.formatted(userConditions, placeList, request.getDuration(), request.getBudget());
    }

    private String buildUserConditions(TravelPlanRequestDto request) {
        StringBuilder sb = new StringBuilder();
        sb.append("- 지역: ").append(request.getRegion()).append("\n");
        sb.append("- 여행 기간: ").append(request.getDuration()).append("일\n");
        sb.append("- 인원: ").append(request.getCompanion()).append("\n");
        sb.append("- 예산: ").append(request.getBudget()).append("원\n");
        sb.append("- 교통: ").append(request.getTransport()).append("\n");
        if (request.getThemes() != null && !request.getThemes().isEmpty()) {
            sb.append("- 스타일: ").append(String.join(", ", request.getThemes())).append("\n");
        }
        return sb.toString();
    }

    private String buildPlaceList(List<TourPlace> candidates) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<TourPlace>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(TourPlace::getCat1));

        List<String> categories = Arrays.asList("관광지", "식사", "숙소");
        for (String category : categories) {
            List<TourPlace> filtered = grouped.getOrDefault(category, List.of());
            if (!filtered.isEmpty()) {
                sb.append("[").append(category).append("]\n");
                filtered.stream()
                        .limit(3) // 카테고리별 최대 3개만 프롬프트에 포함
                        .forEach(place -> sb.append("- ")
                                .append(place.getTitle())
                                // ✅ [수정] getPrice()를 getPrice2()로 변경
                                .append(" (").append(place.getPriceInfo()).append("원)\n"));
            }
        }
        return sb.toString();
    }
}

