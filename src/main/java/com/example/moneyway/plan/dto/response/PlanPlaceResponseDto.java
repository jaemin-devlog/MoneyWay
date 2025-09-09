package com.example.moneyway.plan.dto.response;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.plan.domain.PlanPlace;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class PlanPlaceResponseDto {

    private final Long placeId;
    private final String placeName;
    private final String thumbnailUrl;
    private final String category;
    private final Integer cost;
    private final Integer dayNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private final LocalTime endTime;
    private final Long cartId;
    private final String mapX;
    private final String mapY;

    @Builder
    private PlanPlaceResponseDto(Long placeId, String placeName, String thumbnailUrl, String category, Integer cost, Integer dayNumber, LocalTime startTime, LocalTime endTime, Long cartId, String mapX, String mapY) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.cost = cost;
        this.dayNumber = dayNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cartId = cartId;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public static PlanPlaceResponseDto from(PlanPlace planPlace) {
        Place place = planPlace.getPlace();
        PlaceCategory categoryEnum = place.getCategory();

        // category가 null일 경우를 대비하고, 올바른 getter(getDisplayName)를 사용합니다.
        String categoryName = (categoryEnum != null) ? categoryEnum.getDisplayName() : "기타";

        return PlanPlaceResponseDto.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .thumbnailUrl(place.getThumbnailUrl())
                .category(categoryName)
                .cost(planPlace.getCost())
                .dayNumber(planPlace.getDayNumber())
                .startTime(planPlace.getStartTime())   //  누락된 부분 추가
                .endTime(planPlace.getEndTime())       //  누락된 부분 추가
                .cartId(planPlace.getCartId())
                .mapX(place.getMapX())
                .mapY(place.getMapY())
                .build();
    }
}