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
    private final String username;
    private final int totalPrice;
    private final List<PlanPlaceResponseDto> places;

    @Builder
    private PlanDetailResponseDto(Long id, String title, String username, int totalPrice, List<PlanPlaceResponseDto> places) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.totalPrice = totalPrice;
        this.places = places;
    }

    public static PlanDetailResponseDto from(Plan plan) {
        List<PlanPlaceResponseDto> placeDtos = plan.getPlanPlaces().stream()
                .map(PlanPlaceResponseDto::from)
                .collect(Collectors.toList());

        return PlanDetailResponseDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .username(plan.getUser().getNickname())
                .totalPrice(plan.getTotalPrice())
                .places(placeDtos)
                .build();
    }
}