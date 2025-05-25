package com.example.moneyway.plan.dto.response;

import lombok.Builder;
import lombok.Getter;
import com.example.moneyway.plan.domain.PlanPlace;

@Getter
@Builder
public class PlanPlaceResponse {

    private String placeName;
    private String description;
    private int day;
    private String time;

    public static PlanPlaceResponse from(PlanPlace place) {
        return PlanPlaceResponse.builder()
                .placeName(place.getPlaceName())
                .description(place.getDescription())
                .day(place.getDay())
                .time(place.getTime())
                .build();
    }
}
