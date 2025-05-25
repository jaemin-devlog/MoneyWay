package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanPlaceRequest {
    private String placeName;     // 장소 이름
    private String description;   // 설명
    private int day;              // 여행 며칠째
    private String time;          // 시간대 (예: 오전, 오후 등)
    // 필요 시 setter 또는 생성자 추가 가능
}
