package com.example.moneyway.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GPT가 응답한 하루 일정 단위 DTO
 * ex) "Day 1" → 장소 리스트
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GPTDayPlanDto {
    private String day;  // 예: "Day 1", "Day 2"
    private List<GPTPlaceDto> places;  // 해당 날짜의 장소 리스트
}
