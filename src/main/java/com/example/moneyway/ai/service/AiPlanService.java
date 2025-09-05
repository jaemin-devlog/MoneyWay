package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import com.example.moneyway.ai.dto.response.PlanResponseDto;
import com.example.moneyway.ai.dto.response.PlanSaveResponseDto;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.dto.internal.NearbyPlaceDto;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanPlace;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AiPlanService {

    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;
    private final AiPlanClient openAiClient;

    // txt 템플릿 로드
    private String loadPromptTemplate() throws Exception {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/prompt_template.txt")));
    }

    // GPT 기반 여행 플랜 생성
    public PlanResponseDto generatePlanWithAI(TravelPlanRequestDto request) throws Exception {
        // 1. 중심 관광지 선택
        List<Place> allPlaces = placeRepository.findAll();
        Place center = allPlaces.get(new Random().nextInt(allPlaces.size()));

        // 2. 반경 5km 내 20개 후보 조회
        List<NearbyPlaceDto> nearby = placeRepository.findNearby(center.getMapY(), center.getMapX(), 5.0, 20);

        // 3. 프롬프트 생성
        ObjectMapper mapper = new ObjectMapper();
        String placeJsonString = mapper.writeValueAsString(nearby);

        String template = loadPromptTemplate();
        String filledPrompt = template
                .replace("{places}", placeJsonString)
                .replace("{duration}", String.valueOf(request.getDuration()));

        // 4. GPT 호출
        String aiResponse = openAiClient.requestPlan(filledPrompt);

        // 5. JSON → DTO 매핑
        PlanResponseDto response = mapper.readValue(aiResponse, PlanResponseDto.class);

    // GPT가 준 totalUsedCost 대신, days 안의 usedCost 합계로 다시 계산
        int totalUsed = response.days().stream()
                .mapToInt(DayPlanDto::usedCost)
                .sum();

        return new PlanResponseDto(totalUsed, response.days());

    }

    // 플랜 저장
    @Transactional
    public PlanSaveResponseDto createPlanByAi(AiPlanCreateRequestDto request, User user) throws Exception {
        PlanResponseDto planResponse = generatePlanWithAI(request);

        Plan plan = Plan.builder()
                .title(request.getPlanTitle())
                .budget(request.getBudget())
                .duration(request.getDuration())
                .totalPrice(request.getBudget())
                .usedCost(planResponse.totalUsedCost())
                .user(user)
                .build();

        for (int dayIndex = 0; dayIndex < planResponse.days().size(); dayIndex++) {
            var dayPlan = planResponse.days().get(dayIndex);
            int dayNumber = dayIndex + 1;

            for (var placeDto : dayPlan.places()) {
                Place place = placeRepository.findById(placeDto.placeId()).orElse(null);

                PlanPlace planPlace = PlanPlace.builder()
                        .plan(plan)
                        .place(place)
                        .placeName(placeDto.title())
                        .dayNumber(dayNumber)
                        .cost(placeDto.cost())
                        .type(placeDto.categoryName())
                        .time(placeDto.time())
                        .budget(dayPlan.totalBudget())
                        .totalPrice(dayPlan.usedCost())
                        .startTime(LocalTime.parse(placeDto.startTime()))
                        .endTime(LocalTime.parse(placeDto.endTime()))
                        .build();

                plan.addPlanPlace(planPlace);
            }
        }

        Long planId = planRepository.save(plan).getId();
        return new PlanSaveResponseDto(planId, planResponse);
    }
}
