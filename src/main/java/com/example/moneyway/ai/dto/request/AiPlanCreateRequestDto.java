package com.example.moneyway.ai.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiPlanCreateRequestDto extends TravelPlanRequestDto {
    private String planTitle; // 사용자가 입력한 여행 계획 이름
}