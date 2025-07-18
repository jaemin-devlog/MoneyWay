package com.example.moneyway.plan.dto.response;

import com.example.moneyway.plan.domain.Plan;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PlanDetailResponseDto {

    private final Long id;
    private final String title;
    private final long totalCost;
    private final List<PlanPlaceResponseDto> places;

    @Builder
    private PlanDetailResponseDto(Long id, String title, long totalCost, List<PlanPlaceResponseDto> places) {
        this.id = id;
        this.title = title;
        this.totalCost = totalCost;
        this.places = places;
    }

    public static PlanDetailResponseDto from(Plan plan) {
        List<PlanPlaceResponseDto> placeDtos = plan.getPlanPlaces().stream()
                .map(PlanPlaceResponseDto::from)
                .collect(Collectors.toList());

        long totalCost = placeDtos.stream()
                .mapToLong(PlanPlaceResponseDto::getCost)
                .sum();

        return PlanDetailResponseDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .places(placeDtos)
                .totalCost(totalCost)
                .build();
    }
}