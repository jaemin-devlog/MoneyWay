package com.example.moneyway.plan.dto.response;

import lombok.Builder;
import lombok.Getter;
import com.example.moneyway.place.domain.PlanPlace;

@Getter
@Builder
public class PlanPlaceResponse {

    private Long placeId;      // placeId: 장소 식별자(DB: place_id)
    private int dayIndex;      // dayIndex: 며칠째(DB: day_index)
    private String timeSlot;   // timeSlot: 시간대(DB: time_slot)
    private int orderIndex;    // orderIndex: 하루 중 순서(DB: order_index)
    private int estimatedCost; // estimatedCost: 예상 비용(DB: estimated_cost)
    private int estimatedTime; // estimatedTime: 예상 시간(DB: estimated_time)
    // 필요 시 추가 필드: planId 등

    public static PlanPlaceResponse from(PlanPlace place) {
        return PlanPlaceResponse.builder()
                .placeId(place.getPlaceId())
                .dayIndex(place.getDayIndex())
                .timeSlot(place.getTimeSlot())
                .orderIndex(place.getOrderIndex())
                .estimatedCost(place.getEstimatedCost())
                .estimatedTime(place.getEstimatedTime())
                .build();
    }
}
