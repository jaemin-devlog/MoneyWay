package com.example.moneyway.plan.dto.response;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.plan.domain.PlanPlace;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class PlanPlaceResponseDto {

    private final Long placeId;
    private final String placeName;
    private final String category;
    private final Integer cost;
    private final Integer dayNumber;
    private final LocalTime startTime;
    private final LocalTime endTime;

    @Builder
    private PlanPlaceResponseDto(Long placeId, String placeName, String category, Integer cost, Integer dayNumber, LocalTime startTime, LocalTime endTime) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.category = category;
        this.cost = cost;
        this.dayNumber = dayNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static PlanPlaceResponseDto from(PlanPlace planPlace) {
        Place place = planPlace.getPlace();
        PlaceCategory categoryEnum = place.getCategory();

        // category가 null일 경우를 대비하고, 올바른 getter(getDisplayName)를 사용합니다.
        String categoryName = (categoryEnum != null) ? categoryEnum.getDisplayName() : "기타";

        return PlanPlaceResponseDto.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .category(categoryName)
                .cost(planPlace.getCost())
                .dayNumber(planPlace.getDayNumber())
                .startTime(planPlace.getStartTime())
                .endTime(planPlace.getEndTime())
                .build();
    }
}