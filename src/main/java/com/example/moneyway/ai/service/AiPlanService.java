package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.*;
import com.example.moneyway.place.domain.AiPlaceDto;
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

import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiPlanService {

    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;
    private final AiPlanClient openAiClient;
    private String promptTemplate;
    private final ObjectMapper mapper;

    // txt 템플릿 로드
    private String loadPromptTemplate() throws Exception {
        if (promptTemplate == null) {
            ClassPathResource resource = new ClassPathResource("prompt_template.txt");
            promptTemplate = new String(resource.getInputStream().readAllBytes());
        }
        return promptTemplate;
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

        if (budget <= 0) {
            throw new IllegalArgumentException("예산(budget)은 0보다 커야 합니다.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("여행 기간(duration)은 1일 이상이어야 합니다.");
        }

        int perDayCount = 2;
        int accommodationBudget = (int)(budget * 0.6 / duration);
        int sightseeingBudget   = (int)(budget * 0.2 / duration);
        int foodBudget          = (int)(budget * 0.2 / duration);
        int tourLimit = duration * (perDayCount / 2);
        int foodLimit = duration * perDayCount;

        double radius = 5.0;

        // === 1. 숙소 하나 선택 ===
        NearbyPlaceDto randomAccommodation = placeRepository.findRandomAccommodation(accommodationBudget);

        List<SimplePlaceDto> accommodations = List.of(
                new SimplePlaceDto(
                        randomAccommodation.getId(),
                        randomAccommodation.getTitle(),
                        randomAccommodation.getPriceInfo(),
                        randomAccommodation.getCategoryName(),
                        randomAccommodation.getMapy(),
                        randomAccommodation.getMapx()
                )
        );

        double baseLat = randomAccommodation.getMapy();
        double baseLng = randomAccommodation.getMapx();

        // === 2. 관광지 후보 ===
        List<SimplePlaceDto> tours = placeRepository.findTourAndActivityNearby(
                        sightseeingBudget, baseLat, baseLng, radius
                ).stream()
                .limit(tourLimit)
                .map(p -> new SimplePlaceDto(
                        p.getId(), p.getTitle(), p.getPriceInfo(), p.getCategoryName(),
                        p.getMapy(), p.getMapx()
                ))
                .toList();

        // === 3. 식당 후보 ===
        List<SimplePlaceDto> foods = placeRepository.findRestaurantsNearby(
                        foodBudget, baseLat, baseLng, radius
                ).stream()
                .limit(foodLimit)
                .map(p -> new SimplePlaceDto(
                        p.getId(), p.getTitle(), p.getPriceInfo(), p.getCategoryName(),
                        p.getMapy(), p.getMapx()
                ))
                .toList();

        // === 프롬프트 생성 ===
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

        log.error("Prompt: [{}]", filledPrompt);

        // === GPT 호출 ===
        String aiResponse = openAiClient.requestPlan(filledPrompt);
        log.debug("AI raw response: {}", aiResponse);

        // === 전처리 ===
        String cleaned = aiResponse
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        int firstBrace = cleaned.indexOf("{");
        int lastBrace = cleaned.lastIndexOf("}");
        if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        } else {
            throw new RuntimeException("AI 응답이 JSON 형식이 아님: " + cleaned);
        }
        log.error("Cleaned response: [{}]", cleaned);

        PlanResponseDto response = mapper.readValue(cleaned, PlanResponseDto.class);

        // === 후처리 ===
        int totalUsedCost = 0;
        List<DayPlanDto> fixedDays = new ArrayList<>();
        List<String> requiredSlots = List.of("오전", "점심", "카페", "오후", "저녁", "숙소");

        for (var day : response.days()) {
            List<AiPlaceDto> fixedPlaces = new ArrayList<>(day.places());
            int dayCost = 0;

            // 1. 누락된 슬롯 보정
            for (String slot : requiredSlots) {
                boolean exists = fixedPlaces.stream().anyMatch(p -> slot.equals(p.time()));
                if (!exists) {
                    if ("숙소".equals(slot)) {
                        fixedPlaces.add(new AiPlaceDto(
                                randomAccommodation.getId(),
                                randomAccommodation.getTitle(),
                                null,
                                "ACCOMMODATION",
                                randomAccommodation.getPriceInfo(),
                                String.valueOf(randomAccommodation.getMapy()),
                                String.valueOf(randomAccommodation.getMapx()),
                                "숙소",
                                parsePriceInfo(randomAccommodation.getPriceInfo()),
                                getDefaultStartTime("숙소"),
                                getDefaultEndTime("숙소")
                        ));
                    } else {
                        fixedPlaces.add(new AiPlaceDto(
                                0L,
                                slot + " 자유시간",
                                null,
                                switch (slot) {
                                    case "점심", "저녁" -> "RESTAURANT";
                                    case "카페" -> "CAFE";
                                    default -> "TOURIST_ATTRACTION";
                                },
                                "0",
                                null,
                                null,
                                slot,
                                0,
                                getDefaultStartTime(slot),
                                getDefaultEndTime(slot)
                        ));
                    }
                }
            }

            // 2. time 기준 카테고리 강제 보정
            for (int i = 0; i < fixedPlaces.size(); i++) {
                AiPlaceDto p = fixedPlaces.get(i);
                String forcedCategory = switch (p.time()) {
                    case "점심", "저녁" -> "RESTAURANT";
                    case "카페" -> "CAFE";
                    case "숙소" -> "ACCOMMODATION";
                    default -> "TOURIST_ATTRACTION";
                };

                // 숙소가 오전/오후 슬롯에 들어간 경우 → 관광지로 교체
                if ((p.time().equals("오전") || p.time().equals("오후"))
                        && "ACCOMMODATION".equals(p.categoryName())) {
                    fixedPlaces.set(i, new AiPlaceDto(
                            p.placeId(),
                            p.title() + " (숙소→관광 보정)",
                            p.address(),
                            "TOURIST_ATTRACTION",
                            p.priceInfo(),
                            p.latitude(),
                            p.longitude(),
                            p.time(),
                            p.cost(),
                            p.startTime(),
                            p.endTime()
                    ));
                    continue;
                }

                // 나머지 일반 보정
                if (!forcedCategory.equals(p.categoryName())) {
                    fixedPlaces.set(i, new AiPlaceDto(
                            p.placeId(),
                            p.title(),
                            p.address(),
                            forcedCategory,
                            p.priceInfo(),
                            p.latitude(),
                            p.longitude(),
                            p.time(),
                            p.cost(),
                            p.startTime(),
                            p.endTime()
                    ));
                }
            }

            // 3. 좌표 보강
            List<AiPlaceDto> enrichedPlaces = new ArrayList<>();
            for (AiPlaceDto p : fixedPlaces) {
                String lat = p.latitude();
                String lng = p.longitude();
                if (p.placeId() != 0L && (lat == null || lng == null)) {
                    var placeOpt = placeRepository.findById(p.placeId());
                    if (placeOpt.isPresent()) {
                        var place = placeOpt.get();
                        lat = place.getMapY();
                        lng = place.getMapX();
                    }
                }
                enrichedPlaces.add(new AiPlaceDto(
                        p.placeId(), p.title(), p.address(), p.categoryName(),
                        p.priceInfo(), lat, lng,
                        p.time(), p.cost(),
                        p.startTime(), p.endTime()
                ));
            }

            // 4. 슬롯 순서 정렬
            enrichedPlaces.sort(Comparator.comparingInt(p -> requiredSlots.indexOf(p.time())));

            // 5. 비용 합산
            for (var p : enrichedPlaces) {
                dayCost += p.cost();
            }
            totalUsedCost += dayCost;

            fixedDays.add(new DayPlanDto(day.day(), enrichedPlaces, day.totalBudget(), dayCost));
        }

        return new PlanResponseDto(totalUsedCost, fixedDays, request.getDuration());
    }

    // 기본 시간대 매핑
    private String getDefaultStartTime(String slot) {
        return switch (slot) {
            case "오전" -> "09:00";
            case "점심" -> "11:30";
            case "카페" -> "13:00";
            case "오후" -> "15:30";
//            case "오후2" -> "16:30";
            case "저녁" -> "18:30";
            case "숙소" -> "20:30";
            default -> "09:00";
        };
    }

    private String getDefaultEndTime(String slot) {
        return switch (slot) {
            case "오전" -> "11:00";
            case "점심" -> "12:30";
            case "카페" -> "14:00";
            case "오후" -> "18:00";
//            case "오후2" -> "18:00";
            case "저녁" -> "20:00";
            case "숙소" -> "22:00";
            default -> "10:00";
        };
    }

    // priceInfo → int 변환 유틸
    private int parsePriceInfo(String priceInfo) {
        if (priceInfo == null || priceInfo.isBlank()) return 0;
        try {
            return Integer.parseInt(priceInfo.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
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
