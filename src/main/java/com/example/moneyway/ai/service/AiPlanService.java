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

    private List<SimplePlaceDto> fetchWithDynamicRadius(
            int minRequired,
            int maxRadius,
            int budget,
            double baseLat,
            double baseLng,
            int initialRadius,
            int limit,
            java.util.function.Function<Double, List<NearbyPlaceDto>> fetcher
    ) {
        double radius = initialRadius;
        List<NearbyPlaceDto> results = new ArrayList<>();
        while (radius <= maxRadius) {
            results = fetcher.apply(radius);
            if (results.size() >= minRequired) break;
            log.warn("후보 부족 ({}개) → 반경 {}km → {}km 재시도", results.size(), radius, radius * 2);
            radius *= 1.5; // 점점 늘려가기
        }
        return results.stream()
                .limit(limit)
                .map(p -> new SimplePlaceDto(
                        p.getId(),
                        p.getTitle(),
                        p.getPriceInfo(),
                        p.getCategoryName(),
                        p.getMapy(),
                        p.getMapx()
                ))
                .toList();
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

        int accommodationBudget = (int) (budget * 0.4 / duration);
        int sightseeingBudget = (int) (budget * 0.3 / duration);
        int foodBudget = (int) (budget * 0.2 / duration);
        int cafeBudget = (int) (budget * 0.1 / duration);

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
        log.info("숙소 후보 개수: {}", accommodations.size());

        double baseLat = randomAccommodation.getMapy();
        double baseLng = randomAccommodation.getMapx();

        int tourLimit = duration * 2 + 3;
        int foodLimit = duration * 2 + 3;
        int cafeLimit = duration * 2 + 3;

        // === 2. 관광지 후보 (동적 반경) ===
        List<SimplePlaceDto> tours = fetchWithDynamicRadius(
                8,   // 최소 보장 개수
                15,  // 최대 반경 km
                sightseeingBudget,
                baseLat,
                baseLng,
                10,  // 초기 반경 km
                tourLimit,
                r -> placeRepository.findTourAndActivityNearby(sightseeingBudget, baseLat, baseLng, r)
        );
        log.info("관광지 후보 개수: {}", tours.size());

        // === 3. 식당 후보 (동적 반경) ===
        List<SimplePlaceDto> foods = fetchWithDynamicRadius(
                8,
                15,
                foodBudget,
                baseLat,
                baseLng,
                10,
                foodLimit,
                r -> placeRepository.findRestaurantsNearby(foodBudget, baseLat, baseLng, r)
        );
        log.info("식당 후보 개수: {}", foods.size());

        // === 4. 카페 후보 (동적 반경) ===
        List<SimplePlaceDto> cafes = fetchWithDynamicRadius(
                8,
                15,
                cafeBudget,
                baseLat,
                baseLng,
                10,
                cafeLimit,
                r -> placeRepository.findCafesNearby(cafeBudget, baseLat, baseLng, r)
        );
        log.info("카페 후보 개수: {}", cafes.size());

        // === 프롬프트 생성 ===
        Map<String, Object> placesWrapper = new HashMap<>();
        placesWrapper.put("tourAndActivities", tours);
        placesWrapper.put("restaurants", foods);
        placesWrapper.put("cafes", cafes);
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

        log.debug("Prompt: [{}]", filledPrompt);

        // === GPT 호출 ===
        String aiResponse = openAiClient.requestPlan(filledPrompt);

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

        PlanResponseDto response = mapper.readValue(cleaned, PlanResponseDto.class);

        // 이후 후처리 로직은 그대로 유지
        // === 후처리 ===
        Set<Long> usedPlaceIds = new HashSet<>();
        int totalUsedCost = 0;
        List<DayPlanDto> fixedDays = new ArrayList<>();
        List<String> requiredSlots =
                (request.getDuration() == 1)
                        ? List.of("오전", "점심", "카페", "오후", "저녁")   // 당일치기: 숙소 없음
                        : List.of("오전", "점심", "카페", "오후", "저녁", "숙소"); // 2일 이상: 숙소 포함

        for (var day : response.days()) {
            List<AiPlaceDto> fixedPlaces = new ArrayList<>(day.places());
            int dayCost = 0;

            // 1. 누락된 슬롯 보정 (모든 슬롯 강제 채우기)
            for (String slot : requiredSlots) {
                boolean exists = fixedPlaces.stream().anyMatch(p -> slot.equals(p.time()));
                if (!exists) {
                    if ("숙소".equals(slot)) {
                        // 숙소는 무조건 DB에서 가져오기
                        SimplePlaceDto acc = accommodations.get(0);
                        fixedPlaces.add(new AiPlaceDto(
                                acc.placeId(),
                                acc.title(),
                                null,
                                "ACCOMMODATION",
                                acc.priceInfo(),
                                acc.latitude() != null ? String.valueOf(acc.latitude()) : null,
                                acc.longitude() != null ? String.valueOf(acc.longitude()) : null,
                                "숙소",
                                parsePriceInfo(acc.priceInfo()),
                                getDefaultStartTime("숙소"),
                                getDefaultEndTime("숙소")
                        ));
                        usedPlaceIds.add(acc.placeId());
                    } else {
                        // 나머지는 자유시간 처리
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

            // 2. 이미 존재하는 숙소인데 placeId=0 → DB 숙소로 교체
            for (int i = 0; i < fixedPlaces.size(); i++) {
                AiPlaceDto p = fixedPlaces.get(i);
                if ("ACCOMMODATION".equals(p.categoryName()) && p.placeId() == 0L) {
                    SimplePlaceDto acc = accommodations.get(0);
                    fixedPlaces.set(i, new AiPlaceDto(
                            acc.placeId(),
                            acc.title(),
                            null,
                            "ACCOMMODATION",
                            acc.priceInfo(),
                            acc.latitude() != null ? String.valueOf(acc.latitude()) : null,
                            acc.longitude() != null ? String.valueOf(acc.longitude()) : null,
                            p.time(),
                            parsePriceInfo(acc.priceInfo()),
                            getDefaultStartTime(p.time()),
                            getDefaultEndTime(p.time())
                    ));
                    usedPlaceIds.add(acc.placeId());
                }
            }

            // 3. 일반 장소 중복 제거 (숙소 제외)
            for (int i = 0; i < fixedPlaces.size(); i++) {
                AiPlaceDto p = fixedPlaces.get(i);
                if (p.placeId() != 0L && !"ACCOMMODATION".equals(p.categoryName())) {
                    if (usedPlaceIds.contains(p.placeId())) {
                        fixedPlaces.set(i, new AiPlaceDto(
                                0L,
                                p.time() + " 자유시간",
                                null,
                                p.categoryName(),
                                "0",
                                null,
                                null,
                                p.time(),
                                0,
                                getDefaultStartTime(p.time()),
                                getDefaultEndTime(p.time())
                        ));
                    } else {
                        usedPlaceIds.add(p.placeId());
                    }
                }
            }



            //카페 후보 교체
            for (int i = 0; i < fixedPlaces.size(); i++) {
                AiPlaceDto p = fixedPlaces.get(i);
                if ("CAFE".equals(p.categoryName()) && p.placeId() == 0L) {
                    Optional<SimplePlaceDto> alternativeCafe = cafes.stream()
                            .filter(c -> !usedPlaceIds.contains(c.placeId()))
                            .findAny();

                    if (alternativeCafe.isPresent()) {
                        var alt = alternativeCafe.get();
                        fixedPlaces.set(i, new AiPlaceDto(
                                alt.placeId(),
                                alt.title(),
                                null,
                                "CAFE",
                                alt.priceInfo(),
                                alt.latitude() != null ? String.valueOf(alt.latitude()) : null,
                                alt.longitude() != null ? String.valueOf(alt.longitude()) : null,
                                p.time(),
                                parsePriceInfo(alt.priceInfo()),
                                getDefaultStartTime(p.time()),
                                getDefaultEndTime(p.time())
                        ));
                        usedPlaceIds.add(alt.placeId());
                    }
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
            enrichedPlaces.sort(Comparator.comparingInt(p -> {
                String safeTime = p.time();
                if (safeTime == null) {
                    log.warn("AI 응답에서 time=null 발견. 기본값 '오전'으로 대체합니다. place={}", p.title());
                    safeTime = "오전";
                }
                return requiredSlots.indexOf(safeTime);
            }));

            // 5. 비용 합산
            for (var p : enrichedPlaces) {
                dayCost += p.cost();
            }
            totalUsedCost += dayCost;

            fixedDays.add(new DayPlanDto(day.day(), enrichedPlaces, day.totalBudget(), dayCost));
        }

        // === 6. 최종 예산 보정 ===
        if (totalUsedCost > budget) {
            log.warn("예산 초과 발생. 자유시간으로 일부 교체 필요.");
            totalUsedCost = budget; // 단순하게 budget까지만 인정 (자세히 줄이는 로직 추가 가능)
        }

        return new PlanResponseDto(totalUsedCost, fixedDays, request.getDuration());
    }

    // 기본 시간대 매핑
    private String getDefaultStartTime(String slot) {
        return switch (slot) {
            case "오전" -> "09:00";
            case "점심" -> "11:30";
            case "카페" -> "14:00";
            case "오후" -> "15:30";
            case "저녁" -> "18:30";
            case "숙소" -> "20:30";
            default -> "09:00";
        };
    }

    private String getDefaultEndTime(String slot) {
        return switch (slot) {
            case "오전" -> "11:00";
            case "점심" -> "12:30";
            case "카페" -> "15:00";
            case "오후" -> "18:00";
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
