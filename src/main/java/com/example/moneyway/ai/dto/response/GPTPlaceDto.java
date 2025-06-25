package com.example.moneyway.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GPT가 응답한 하루 일정 내 장소 단위 DTO
 * ex) { "place": "용두암", "type": "관광지", "time": "오전", "cost": 0 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GPTPlaceDto {
    private String place;  // 장소명
    private String type;   // 장소 유형 (관광지, 식사, 숙소)
    private String time;   // 시간대 (오전, 점심, 오후, 저녁, 숙소)
    private int cost;      // 비용
}
