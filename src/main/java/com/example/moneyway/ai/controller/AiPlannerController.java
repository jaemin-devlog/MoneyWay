package com.example.moneyway.api;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.GPTDayPlanDto;
import com.example.moneyway.ai.service.AiPlannerService;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.service.TourPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GPT 기반 여행 일정 추천 요청을 처리하는 API 컨트롤러
 */
@RestController
@RequestMapping("/api/ai/planner")
@RequiredArgsConstructor
public class AiPlannerController {

    private final AiPlannerService aiPlannerService;
    private final TourPlaceService tourPlaceService; // 장소 후보 필터링용 서비스

    /**
     * GPT 일정 추천 요청을 처리하는 엔드포인트
     *
     * @param request 사용자 입력 조건 DTO
     * @return 추천된 일정 리스트
     */
    @PostMapping
    public List<GPTDayPlanDto> getPlan(@RequestBody TravelPlanRequestDto request) {
        // 1. 사용자 조건에 따라 장소 후보 필터링
        List<TourPlace> candidates = tourPlaceService.findCandidatesByRequest(request);

        // 2. GPT 일정 추천
        return aiPlannerService.getRecommendedPlan(request, candidates);
    }
}
