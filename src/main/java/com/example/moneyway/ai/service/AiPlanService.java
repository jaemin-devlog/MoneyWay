package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.PlanResponseDto;
import com.example.moneyway.ai.dto.response.PlanSaveResponseDto;
import com.example.moneyway.place.dto.internal.NearbyPlaceDto;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiPlanService {

    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;
    private final AiPlanClient openAiClient;

    // txt 템플릿 로드
    private String loadPromptTemplate() throws Exception {
        ClassPathResource resource = new ClassPathResource("prompt_template.txt");
        return new String(resource.getInputStream().readAllBytes());
    }

    // 안전한 시간 파싱
    private LocalTime safeParse(String value, LocalTime defaultValue) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return LocalTime.parse(value);
        } catch (Exception e) {
            log.warn("시간 파싱 실패: '{}', 기본값 사용: {}", value, defaultValue);
            return defaultValue;
        }
    }

    // GPT 기반 여행 플랜 생성
    public PlanResponseDto generatePlanWithAI(TravelPlanRequestDto request) throws Exception {

        int budget = request.getBudget();
        int duration = request.getDuration();

        // 비율 분배 (60% 숙소, 20% 관광, 20% 식사) — 하루 단위로 계산
        int accommodationBudget = (int)(budget * 0.6 / duration);
        int sightseeingBudget   = (int)(budget * 0.2 / duration);
        int foodBudget          = (int)(budget * 0.2 / duration);

        // 예산 내에서 랜덤 후보 20개 가져오기
        List<NearbyPlaceDto> tours = placeRepository.findTourAndActivity(sightseeingBudget);
        List<NearbyPlaceDto> foods = placeRepository.findRestaurants(foodBudget);
        List<NearbyPlaceDto> accommodations = placeRepository.findAccommodations(accommodationBudget);

        log.debug("숙소 후보 개수: {}", accommodations.size());

        // 프롬프트 생성
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> placesWrapper = new HashMap<>();
        placesWrapper.put("tourAndActivities", tours);
        placesWrapper.put("restaurants", foods);
        placesWrapper.put("accommodations", accommodations);

        String placeJsonString = mapper.writeValueAsString(placesWrapper);

        String template = loadPromptTemplate();
        String filledPrompt = template
                .replace("{places}", placeJsonString)
                .replace("{duration}", String.valueOf(duration))
                .replace("{budget}", String.valueOf(budget))
                .replace("{accommodationBudget}", String.valueOf(accommodationBudget))
                .replace("{sightseeingBudget}", String.valueOf(sightseeingBudget))
                .replace("{foodBudget}", String.valueOf(foodBudget));

        log.debug("Prompt to AI: {}", filledPrompt);

        // GPT 호출
        String aiResponse = openAiClient.requestPlan(filledPrompt);
        log.debug("AI raw response: {}", aiResponse);

        // JSON → DTO 매핑
        PlanResponseDto response = mapper.readValue(aiResponse, PlanResponseDto.class);

        // 서버에서 usedCost, totalUsedCost 재계산
        int totalUsedCost = 0;
        List<com.example.moneyway.ai.dto.response.DayPlanDto> fixedDays = new ArrayList<>();

        for (var day : response.days()) {
            int dayCost = day.places().stream()
                    .mapToInt(p -> p.cost() != 0 ? p.cost() : parsePriceInfo(p.priceInfo()))
                    .sum();

            totalUsedCost += dayCost;

            fixedDays.add(new com.example.moneyway.ai.dto.response.DayPlanDto(
                    day.day(),
                    day.places(),
                    day.totalBudget(),
                    dayCost
            ));
        }

        return new PlanResponseDto(totalUsedCost, fixedDays);
    }

    // priceInfo → int 변환 유틸 (예: "25000" → 25000, "무료" → 0)
    private int parsePriceInfo(String priceInfo) {
        if (priceInfo == null || priceInfo.isBlank()) return 0;
        try {
            return Integer.parseInt(priceInfo.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    // 플랜 저장
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

        //  Day / Place 저장
        for (int dayIndex = 0; dayIndex < planResponse.days().size(); dayIndex++) {
            var dayPlan = planResponse.days().get(dayIndex);
            int dayNumber = dayIndex + 1;

            for (var placeDto : dayPlan.places()) {
                // placeId == 0 인 경우(카페, 자유시간 등)는 DB에 없는 장소라 null 허용
                var place = (placeDto.placeId() != 0)
                        ? placeRepository.findById(placeDto.placeId()).orElse(null)
                        : null;

                var planPlace = com.example.moneyway.plan.domain.PlanPlace.builder()
                        .plan(plan)
                        .place(place)
                        .placeName(placeDto.title())
                        .dayNumber(dayNumber)
                        .cost(placeDto.cost() != 0 ? placeDto.cost() : parsePriceInfo(placeDto.priceInfo()))
                        .type(placeDto.categoryName())
                        .time(placeDto.time())
                        .budget(dayPlan.totalBudget())
                        .totalPrice(plan.getBudget())
                        .startTime(safeParse(placeDto.startTime(), LocalTime.of(9, 0)))
                        .endTime(safeParse(placeDto.endTime(), LocalTime.of(18, 0)))
                        .build();

                plan.addPlanPlace(planPlace);
            }
        }

        Long planId = planRepository.save(plan).getId();
        return new PlanSaveResponseDto(planId, planResponse);
    }

}
