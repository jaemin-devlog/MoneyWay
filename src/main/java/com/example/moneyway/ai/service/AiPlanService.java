package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import com.example.moneyway.ai.dto.response.PlaceDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiPlanService {

    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;
    private final AiPlanClient openAiClient;

    // txt 템플릿 로드
    private String loadPromptTemplate() throws Exception {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/prompt_template.txt")));
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
    // GPT 기반 여행 플랜 생성
    public PlanResponseDto generatePlanWithAI(TravelPlanRequestDto request) throws Exception {

        int budget = request.getBudget();
        int duration = request.getDuration();

        // 비율 분배 (60% 숙소, 20% 관광, 20% 식사)
        int accommodationBudget = (int)(budget * 0.6);
        int sightseeingBudget = (int)(budget * 0.2);
        int foodBudget = (int)(budget * 0.2);

        List<NearbyPlaceDto> tours = placeRepository.findTourAndActivity(20);
        List<NearbyPlaceDto> foods = placeRepository.findRestaurants(20);

        // ✅ 숙소는 한 개만 선택
        List<NearbyPlaceDto> accommodations = placeRepository.findAccommodations(20);
        NearbyPlaceDto selectedAccommodation = accommodations.isEmpty() ? null : accommodations.get(0);

        log.debug("숙소 후보 개수: {}", accommodations.size());
        if (selectedAccommodation != null) {
            log.debug("선택된 숙소: {} ({})", selectedAccommodation.getTitle(), selectedAccommodation.getId());
        }

        // 4. 프롬프트 생성
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> placesWrapper = new HashMap<>();
        placesWrapper.put("tourAndActivities", tours);
        placesWrapper.put("restaurants", foods);
        if (selectedAccommodation != null) {
            placesWrapper.put("accommodations", List.of(selectedAccommodation)); // 하나만 전달
        }

        String placeJsonString = mapper.writeValueAsString(placesWrapper);

        String template = loadPromptTemplate();
        String filledPrompt = template
                .replace("{places}", placeJsonString)
                .replace("{duration}", String.valueOf(duration))
                .replace("{budget}", String.valueOf(budget));

        log.debug("Prompt to AI: {}", filledPrompt);

        // 5. GPT 호출
        String aiResponse = openAiClient.requestPlan(filledPrompt);
        log.debug("AI raw response: {}", aiResponse);

        // 6. JSON → DTO 매핑
        PlanResponseDto response = mapper.readValue(aiResponse, PlanResponseDto.class);

        // 응답 검증
        if (response.days() == null || response.days().isEmpty()) {
            throw new IllegalStateException("AI 응답에 days가 없습니다.");
        }
        if (response.days().size() < duration) {
            throw new IllegalStateException(
                    String.format("AI 응답 일수 부족: 요청 %d일, 응답 %d일",
                            duration, response.days().size())
            );
        }

        // DB 유효 placeId
        Set<Long> validIds = new HashSet<>();
        validIds.addAll(tours.stream().map(NearbyPlaceDto::getId).toList());
        validIds.addAll(foods.stream().map(NearbyPlaceDto::getId).toList());
        if (selectedAccommodation != null) {
            validIds.add(selectedAccommodation.getId());
        }

        int perDayBudget = budget / duration;

        List<DayPlanDto> fixedDays = response.days().stream()
                .map(day -> {
                    List<PlaceDto> filteredPlaces = day.places().stream()
                            .filter(p -> validIds.contains(p.placeId()))
                            .toList();

                    int dayCost = filteredPlaces.stream().mapToInt(PlaceDto::cost).sum();

                    // 하루 예산 초과 시 비싼 곳 제거
                    if (dayCost > perDayBudget) {
                        List<PlaceDto> adjusted = new ArrayList<>();
                        int running = 0;
                        List<PlaceDto> sorted = new ArrayList<>(filteredPlaces);
                        sorted.sort(Comparator.comparingInt(PlaceDto::cost));

                        for (PlaceDto p : sorted) {
                            if (running + p.cost() <= perDayBudget) {
                                adjusted.add(p);
                                running += p.cost();
                            } else {
                                log.info("예산 초과로 제외된 장소: {} ({}원)", p.title(), p.cost());
                            }
                        }
                        return new DayPlanDto(day.day(), adjusted, perDayBudget, running);
                    }

                    return new DayPlanDto(day.day(), filteredPlaces, perDayBudget, dayCost);
                })
                .toList();

        int totalUsed = fixedDays.stream().mapToInt(DayPlanDto::usedCost).sum();

        // 전체 예산 초과 시 숙소 비용 우선 줄이기
        if (totalUsed > budget) {
            log.warn("총 예산 초과: {} / {}", totalUsed, budget);

            List<DayPlanDto> adjustedDays = fixedDays.stream()
                    .map(day -> {
                        List<PlaceDto> adjustedPlaces = new ArrayList<>(day.places());

                        Optional<PlaceDto> accommodation = adjustedPlaces.stream()
                                .filter(p -> "ACCOMMODATION".equalsIgnoreCase(p.categoryName()))
                                .findFirst();

                        if (accommodation.isPresent()) {
                            PlaceDto acc = accommodation.get();
                            if (acc.cost() > accommodationBudget) {
                                int newCost = Math.min(acc.cost(), accommodationBudget);
                                PlaceDto cheaperAcc = new PlaceDto(
                                        acc.placeId(), acc.title(), acc.address(),
                                        acc.thumbnailUrl(), acc.thumbnailUrl2(),
                                        acc.categoryName(), acc.priceInfo(),
                                        acc.latitude(), acc.longitude(),
                                        acc.time(), newCost,
                                        acc.startTime(), acc.endTime()
                                );
                                adjustedPlaces = adjustedPlaces.stream()
                                        .map(p -> p.equals(acc) ? cheaperAcc : p)
                                        .toList();
                            }
                        }

                        int cost = adjustedPlaces.stream().mapToInt(PlaceDto::cost).sum();
                        return new DayPlanDto(day.day(), adjustedPlaces, perDayBudget, cost);
                    })
                    .toList();

            totalUsed = adjustedDays.stream().mapToInt(DayPlanDto::usedCost).sum();
            return new PlanResponseDto(totalUsed, adjustedDays);
        }

        return new PlanResponseDto(totalUsed, fixedDays);
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
