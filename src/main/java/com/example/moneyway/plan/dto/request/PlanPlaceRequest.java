package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PlanPlaceRequest {
    private Long placeId;
    private int dayIndex;
    private String timeSlot;
    private int orderIndex;
    private int estimatedCost;
    private int estimatedTime;
}
