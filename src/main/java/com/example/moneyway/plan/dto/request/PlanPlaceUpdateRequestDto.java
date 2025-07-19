package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class PlanPlaceUpdateRequestDto {
    private Long placeId; // 장소의 고유 ID
    private Integer cost; // 수정된 예상 비용
    private int dayNumber; // 몇일차인지
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
}
