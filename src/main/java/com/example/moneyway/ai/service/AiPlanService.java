package com.example.moneyway.ai.service;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import com.example.moneyway.ai.dto.response.PlaceDto;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.place.service.PlaceQueryService;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPlanService {
    private final PlaceQueryService placeQueryService;
    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;

    public List<DayPlanDto> generatePlan(TravelPlanRequestDto request) {
        int totalBudget = request.getBudget();
        int totalDays = request.getDuration();

        List<Place> allPlaces = placeRepository.findAll();

        List<Place> validPlaces = allPlaces.stream()
                .filter(p -> p.getNumericPrice() > 0)
                .filter(p -> p.getCategory() != null)
                .toList();

        Map<String, List<Place>> grouped = validPlaces.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getDisplayName()));

        log.info("== 그룹핑된 카테고리 목록 ==");
        grouped.forEach((key, value) -> log.info("카테고리: " + key + ", 개수: " + value.size()));

        // 숙소 고급 선호
        List<Place> lodgings = grouped.getOrDefault("숙소", Collections.emptyList()).stream()
                .filter(p -> p.getPlaceName().contains("호텔"))
                .sorted((a, b) -> Integer.compare(b.getNumericPrice(), a.getNumericPrice()))
                .collect(Collectors.toList());

        if (lodgings.isEmpty()) {
            throw new IllegalArgumentException("예산 내 고급 호텔 숙소가 없습니다.");
        }

        // 숙소 예산 최대한 활용
        Place selectedLodging = lodgings.get(0);
        for (Place lodging : lodgings) {
            if (lodging.getNumericPrice() * totalDays <= totalBudget) {
                selectedLodging = lodging;
                break;
            }
        }

        int lodgingPerNight = selectedLodging.getNumericPrice();
        int totalLodgingCost = lodgingPerNight * totalDays;
        int activityBudget = totalBudget - totalLodgingCost;

        int dailyActivityBudget = activityBudget / totalDays;

        double ratioMorning = 0.20;
        double ratioLunch = 0.20;
        double ratioAfternoon = 0.25;
        double ratioDinner = 0.20;
        double ratioCafe = 0.10;

        List<DayPlanDto> result = new ArrayList<>();

        for (int day = 1; day <= totalDays; day++) {
            List<PlaceDto> dayPlaces = new ArrayList<>();
            int usedBudget = 0;

            int budgetMorning = (int) (dailyActivityBudget * ratioMorning);
            int budgetLunch = (int) (dailyActivityBudget * ratioLunch);
            int budgetAfternoon = (int) (dailyActivityBudget * ratioAfternoon);
            int budgetDinner = (int) (dailyActivityBudget * ratioDinner);
            int budgetCafe = (int) (dailyActivityBudget * ratioCafe);

            // 오전
            usedBudget += addOptimizedActivity(grouped.get("관광지"), budgetMorning, "오전", 1, dayPlaces);
            // 점심
            usedBudget += addOptimizedActivity(grouped.get("식당"), budgetLunch, "점심", 1, dayPlaces);
            // 오후
            List<Place> afternoonMix = new ArrayList<>();
            if (grouped.get("액티비티/체험") != null) afternoonMix.addAll(grouped.get("액티비티/체험"));
            if (grouped.get("관광지") != null) afternoonMix.addAll(grouped.get("관광지"));
            usedBudget += addOptimizedActivity(afternoonMix, budgetAfternoon, "오후", 1, dayPlaces);
            // 카페
            usedBudget += addOptimizedActivity(grouped.get("카페"), budgetCafe, "카페", 1, dayPlaces);
            // 저녁
            usedBudget += addOptimizedActivity(grouped.get("식당"), budgetDinner, "저녁", 1, dayPlaces);

            // 예산 남으면 오전/오후 추가
            int remaining = dailyActivityBudget - usedBudget;
            if (remaining > 0) {
                usedBudget += addOptimizedActivity(grouped.get("관광지"), remaining / 2, "추가 활동", 1, dayPlaces);
                usedBudget += addOptimizedActivity(afternoonMix, remaining / 2, "추가 활동", 1, dayPlaces);
            }

            // 숙소 추가
            dayPlaces.add(new PlaceDto(
                    selectedLodging.getPlaceName(),
                    selectedLodging.getCategory().getDisplayName(),
                    "숙소",
                    lodgingPerNight
            ));

            result.add(new DayPlanDto(day + "일차", dayPlaces));
        }

        return result;
    }

    private int addOptimizedActivity(List<Place> list, int maxBudget, String time, int maxCount, List<PlaceDto> target) {
        if (list == null || list.isEmpty()) return 0;

        List<Place> filtered = list.stream()
                .filter(p -> p.getNumericPrice() <= maxBudget)
                .collect(Collectors.toList());

        Collections.shuffle(filtered);
        int used = 0, count = 0;
        for (Place p : filtered) {
            if (count >= maxCount) break;
            int price = p.getNumericPrice();
            if (used + price <= maxBudget) {
                target.add(new PlaceDto(p.getPlaceName(), p.getCategory().getDisplayName(), time, price));
                used += price;
                count++;
            }
        }
        return used;
    }

    @Transactional
    public Long createPlanByAi(AiPlanCreateRequestDto request, User user) {
        List<DayPlanDto> dayPlanDtos = generatePlan(request);
        Plan savedPlan = planRepository.save(null); // TODO: 실제 저장 로직 구현 필요
        return savedPlan.getId();
    }
}
