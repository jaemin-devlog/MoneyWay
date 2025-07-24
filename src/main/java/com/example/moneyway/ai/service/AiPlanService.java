package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import com.example.moneyway.ai.dto.response.PlaceDto;
import com.example.moneyway.ai.dto.response.PlanResponseDto;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanPlace;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPlanService {

    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;

    private static final double EARTH_RADIUS = 6371.0; // km

    /** 거리 계산 (Haversine 공식) */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /** 안전한 좌표 파싱 */
    private Double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 시간대 매핑 로직 */
    private final Map<String, LocalTime[]> timeMap = new HashMap<>() {{
        put("오전", new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(11, 0)});
        put("점심", new LocalTime[]{LocalTime.of(11, 30), LocalTime.of(13, 0)});
        put("카페", new LocalTime[]{LocalTime.of(13, 30), LocalTime.of(14, 30)});
        put("오후1", new LocalTime[]{LocalTime.of(14, 30), LocalTime.of(16, 0)});
        put("오후2", new LocalTime[]{LocalTime.of(16, 0), LocalTime.of(17, 30)});
        put("저녁", new LocalTime[]{LocalTime.of(18, 0), LocalTime.of(19, 30)});
        put("숙소", new LocalTime[]{LocalTime.of(20, 0), LocalTime.of(23, 0)});
    }};


    /** 여행 플랜 생성 */
    public PlanResponseDto generatePlan(TravelPlanRequestDto request) {
        int totalBudget = request.getBudget();
        int totalDays = request.getDuration();

        List<Place> allPlaces = placeRepository.findAll();
        List<Place> validPlaces = allPlaces.stream()
                .filter(p -> p.getNumericPrice() > 0 && p.getCategory() != null)
                .collect(Collectors.toList());

        Map<String, List<Place>> grouped = validPlaces.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getDisplayName()));

        // 숙소 선택
        List<Place> lodgings = grouped.getOrDefault("숙소", Collections.emptyList()).stream()
                .filter(p -> p.getPlaceName().contains("호텔"))
                .filter(p -> p.getNumericPrice() * totalDays <= totalBudget)
                .collect(Collectors.toList());

        if (lodgings.isEmpty()) {
            throw new IllegalArgumentException("예산 내 숙소가 없습니다.");
        }

        Place selectedLodging = lodgings.get(new Random().nextInt(lodgings.size()));
        int lodgingPerNight = selectedLodging.getNumericPrice();
        int totalLodgingCost = lodgingPerNight * totalDays;
        int activityBudget = totalBudget - totalLodgingCost;
        int dailyActivityBudget = activityBudget / totalDays;

        Double baseLat = safeParseDouble(selectedLodging.getMapY());
        Double baseLon = safeParseDouble(selectedLodging.getMapX());

        double ratioMorning = 0.20, ratioLunch = 0.20, ratioAfternoon = 0.25, ratioDinner = 0.20, ratioCafe = 0.10;

        List<DayPlanDto> result = new ArrayList<>();
        int totalUsedCost = 0;

        for (int day = 1; day <= totalDays; day++) {
            List<PlaceDto> dayPlaces = new ArrayList<>();
            int usedBudget = 0;

            usedBudget += addOptimizedActivity(grouped.get("관광지"), (int) (dailyActivityBudget * ratioMorning), "오전", dayPlaces, baseLat, baseLon);
            usedBudget += addOptimizedActivity(grouped.get("식당"), (int) (dailyActivityBudget * ratioLunch), "점심", dayPlaces, baseLat, baseLon);
            usedBudget += addOptimizedActivity(grouped.get("카페"), (int) (dailyActivityBudget * ratioCafe), "카페", dayPlaces, baseLat, baseLon);

            List<Place> afternoonMix = new ArrayList<>();
            if (grouped.get("액티비티/체험") != null) afternoonMix.addAll(grouped.get("액티비티/체험"));
            if (grouped.get("관광지") != null) afternoonMix.addAll(grouped.get("관광지"));

            usedBudget += addOptimizedActivity(afternoonMix, (int) (dailyActivityBudget * ratioAfternoon / 2), "오후1", dayPlaces, baseLat, baseLon);
            usedBudget += addOptimizedActivity(afternoonMix, (int) (dailyActivityBudget * ratioAfternoon / 2), "오후2", dayPlaces, baseLat, baseLon);

            usedBudget += addOptimizedActivity(grouped.get("식당"), (int) (dailyActivityBudget * ratioDinner), "저녁", dayPlaces, baseLat, baseLon);

            dayPlaces.add(new PlaceDto(selectedLodging.getPlaceName(), selectedLodging.getCategory().getDisplayName(), "숙소", lodgingPerNight));

            int dayUsedCost = dayPlaces.stream().mapToInt(PlaceDto::cost).sum();
            totalUsedCost += dayUsedCost;

            result.add(new DayPlanDto(day + "일차", dayPlaces, totalBudget, dayUsedCost));
        }

        return new PlanResponseDto(totalUsedCost, result);
    }

    private int addOptimizedActivity(List<Place> list, int maxBudget, String time, List<PlaceDto> target, double baseLat, double baseLon) {
        if (list == null || list.isEmpty()) return 0;

        List<Place> filtered = list.stream()
                .filter(p -> p.getNumericPrice() <= maxBudget)
                .filter(p -> {
                    Double lat = safeParseDouble(p.getMapY());
                    Double lon = safeParseDouble(p.getMapX());
                    return lat != null && lon != null && calculateDistance(baseLat, baseLon, lat, lon) <= 5.0;
                })
                .collect(Collectors.toList()); // 불변 리스트 → 가변 리스트로 변경

        if (filtered.isEmpty()) return 0;
        Collections.shuffle(filtered);

        Place p = filtered.get(0);
        target.add(new PlaceDto(p.getPlaceName(), p.getCategory().getDisplayName(), time, p.getNumericPrice()));

        return p.getNumericPrice();
    }

    /** 플랜 저장 */
    @Transactional
    public Long createPlanByAi(AiPlanCreateRequestDto request, User user) {
        PlanResponseDto planResponse = generatePlan(request);

        Plan plan = Plan.builder()
                .title(request.getPlanTitle())
                .budget(request.getBudget())
                .duration(request.getDuration())
                .totalPrice(request.getBudget())
                .usedCost(planResponse.totalUsedCost())
                .user(user)
                .build();

        for (int dayIndex = 0; dayIndex < planResponse.days().size(); dayIndex++) {
            DayPlanDto dayPlan = planResponse.days().get(dayIndex);
            int dayNumber = dayIndex + 1;

            for (PlaceDto placeDto : dayPlan.places()) {
                List<Place> places = placeRepository.findByTitle(placeDto.place());
                Place place = places.isEmpty() ? null : places.get(0);
                LocalTime[] times = timeMap.getOrDefault(placeDto.time(), new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(10, 0)});

                PlanPlace planPlace = PlanPlace.builder()
                        .plan(plan)
                        .place(place)
                        .dayNumber(dayNumber)
                        .cost(placeDto.cost())
                        .type(placeDto.type())
                        .time(placeDto.time())
                        .budget(dayPlan.totalBudget())
                        .totalPrice(dayPlan.usedCost())
                        .startTime(times[0])
                        .endTime(times[1])
                        .build();

                plan.addPlanPlace(planPlace);
            }
        }

        return planRepository.save(plan).getId();
    }
}
