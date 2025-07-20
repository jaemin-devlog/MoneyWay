package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.GPTDayPlanDto;
import com.example.moneyway.ai.dto.response.GPTPlaceDto;
import com.example.moneyway.ai.support.PromptBuilder;
import com.example.moneyway.common.exception.CustomException.CustomGptException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.infrastructure.openai.OpenAiClient;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.place.service.PlaceQueryService;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanPlace;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPlannerService {

    private final PromptBuilder promptBuilder;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;
    private final PlaceQueryService placeQueryService;
    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;

    public List<GPTDayPlanDto> getRecommendedPlan(TravelPlanRequestDto request) {
        List<Place> candidates = placeQueryService.findCandidatesByRequest(request);
        log.info("사용자 요청 기반 장소 후보 {}건 필터링 완료", candidates.size());

        String prompt = promptBuilder.buildPrompt(request, candidates);
        log.info("프롬프트 생성 완료");

        String response = openAiClient.call(prompt);
        log.info("GPT 응답 수신 완료");

        return parseResponse(response);
    }

    @Transactional
    public Long createPlanByAi(AiPlanCreateRequestDto request, User user) {
        List<GPTDayPlanDto> gptDayPlans = getRecommendedPlan(request);

        Plan plan = convertGptResponseToPlan(gptDayPlans, request.getPlanTitle(), user, request);

        Plan savedPlan = planRepository.save(plan);
        log.info("AI 기반 여행 계획 생성 완료. Plan ID: {}", savedPlan.getId());

        return savedPlan.getId();
    }

    private Plan convertGptResponseToPlan(List<GPTDayPlanDto> gptDayPlans, String title, User user, AiPlanCreateRequestDto request) {
        Plan plan = Plan.builder()
                .title(title)
                .user(user)
                .planPlaces(new ArrayList<>())
                .build();

        List<String> placeTitles = gptDayPlans.stream()
                .flatMap(day -> day.places().stream())
                .map(GPTPlaceDto::place)
                .distinct()
                .toList();
        Map<String, Place> placeMap = placeRepository.findAllByTitleIn(placeTitles).stream()
                .collect(Collectors.toMap(Place::getTitle, p -> p));

        // 각 날짜별 시간대별 활동 카운트 및 시작 시간 추적
        Map<Integer, Map<String, Integer>> activityCount = new HashMap<>();
        Map<Integer, Map<String, LocalTime>> timeTracker = new HashMap<>();

        // 전체 계획에서 중복 장소를 추적하기 위한 Set
        Set<Long> addedPlaceIds = new HashSet<>();

        for (GPTDayPlanDto gptDay : gptDayPlans) {
            Integer currentDayNumber = null; // Integer 타입으로 선언하여 null 허용
            if ("숙소".equals(gptDay.day())) {
                currentDayNumber = request.getDuration(); // 숙소는 여행의 마지막 날짜로 설정
            } else {
                currentDayNumber = Integer.parseInt(gptDay.day().replaceAll("[^0-9]", ""));
            }

            // activityCount와 timeTracker는 유효한 dayNumber에 대해서만 초기화 및 사용
            if (currentDayNumber != null) {
                activityCount.putIfAbsent(currentDayNumber, new HashMap<>());
                timeTracker.putIfAbsent(currentDayNumber, new HashMap<>());

                timeTracker.get(currentDayNumber).put("오전", LocalTime.of(9, 0));
                timeTracker.get(currentDayNumber).put("오후", LocalTime.of(14, 0));
            }

            for (GPTPlaceDto gptPlace : gptDay.places()) {
                Place placeEntity = placeMap.get(gptPlace.place());
                if (placeEntity == null) {
                    log.warn("AI가 추천한 장소 '{}'를 DB에서 찾을 수 없습니다. 계획에서 제외됩니다.", gptPlace.place());
                    continue;
                }

                // 장소 중복 검사 (숙소는 중복 검사에서 제외)
                if (placeEntity.getCategory() != PlaceCategory.ACCOMMODATION && addedPlaceIds.contains(placeEntity.getId())) {
                    log.warn("AI가 추천한 장소 '{}' (ID: {})는 이미 계획에 추가되어 중복을 방지하기 위해 제외됩니다.", gptPlace.place(), placeEntity.getId());
                    continue;
                }

                LocalTime startTime = null;
                LocalTime endTime = null;

                if (placeEntity.getCategory() == PlaceCategory.ACCOMMODATION) {
                    // 숙소는 특정 기본 시간 할당 (예: 21:00 ~ 22:00)
                    startTime = LocalTime.of(21, 0);
                    endTime = LocalTime.of(22, 0);
                } else {
                    String timeOfDay = gptPlace.time();
                    
                    if (currentDayNumber != null && ("오전".equals(timeOfDay) || "오후".equals(timeOfDay))) {
                        int count = activityCount.get(currentDayNumber).getOrDefault(timeOfDay, 0);
                        int max = "오전".equals(timeOfDay) ? 2 : 3;

                        if (count < max) {
                            startTime = timeTracker.get(currentDayNumber).get(timeOfDay);
                            endTime = startTime.plusHours(2);
                            timeTracker.get(currentDayNumber).put(timeOfDay, endTime);
                            activityCount.get(currentDayNumber).put(timeOfDay, count + 1);
                        } else {
                            log.warn("{}일차 {} 활동 개수 초과. 장소 '{}'는 계획에서 제외됩니다.", currentDayNumber, timeOfDay, gptPlace.place());
                            continue;
                        }
                    } else {
                        TimeSlot slot = mapTimeToTimeSlot(timeOfDay);
                        startTime = slot.startTime();
                        endTime = slot.endTime();
                    }
                }

                PlanPlace planPlace = PlanPlace.builder()
                        .place(placeEntity)
                        .dayNumber(currentDayNumber) // null이 될 수 있음
                        .startTime(startTime)
                        .endTime(endTime)
                        .cost(gptPlace.cost())
                        .build();

                plan.addPlanPlace(planPlace);
                // 장소가 성공적으로 추가되면 Set에 ID를 기록
                addedPlaceIds.add(placeEntity.getId());
            }
        }
        return plan;
    }

    private record TimeSlot(LocalTime startTime, LocalTime endTime) {}

    private TimeSlot mapTimeToTimeSlot(String time) {
        return switch (time) {
            case "오전" -> new TimeSlot(LocalTime.of(9, 0), LocalTime.of(12, 0));
            case "점심" -> new TimeSlot(LocalTime.of(12, 0), LocalTime.of(14, 0));
            case "오후" -> new TimeSlot(LocalTime.of(14, 0), LocalTime.of(18, 0));
            case "저녁" -> new TimeSlot(LocalTime.of(18, 0), LocalTime.of(20, 0));
            case "숙소" -> new TimeSlot(LocalTime.of(21, 0), LocalTime.of(22, 0));
            default -> new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0));
        };
    }

    private List<GPTDayPlanDto> parseResponse(String response) {
        log.debug("OpenAI Raw Response: {}", response); // ✅ [추가] OpenAI 원본 응답 로그
        try {
            String jsonResponse = extractJson(response);

            Map<String, List<GPTPlaceDto>> dayMap = objectMapper.readValue(
                    jsonResponse, new TypeReference<>() {}
            );

            return dayMap.entrySet().stream()
                    .map(entry -> new GPTDayPlanDto(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            log.error("GPT 응답 JSON 파싱 실패. 응답: {}", response, e);
            throw new CustomGptException(ErrorCode.GPT_GENERATION_FAILED, e);
        }
    }

    private String extractJson(String str) {
        int firstBrace = str.indexOf('{');
        int lastBrace = str.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return str.substring(firstBrace, lastBrace + 1);
        }
        return str;
    }
}
