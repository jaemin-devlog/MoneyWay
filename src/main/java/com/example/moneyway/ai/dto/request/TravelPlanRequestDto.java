/*
 * 사용자가 입력한 여행 조건(예산, 기간, 동행자, 스타일 등)을 담는 요청 DTO.
 * GPT 프롬프트 생성 시 핵심 입력값으로 사용되며, 추천 일정 필터링 및 장소 후보 조회에도 활용된다.
 */

package com.example.moneyway.ai.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class TravelPlanRequestDto {

    private int budget;                 // 총 예산 (예: 200000)
    private String duration;           // 여행 기간 (예: "1박2일", "2박3일")
    private String region;             // 여행 지역 (예: "제주도")
    private String companion;          // 동행자 유형 (예: "연인", "가족", "친구")
    private List<String> themes;       // 여행 스타일 (예: ["맛집", "자연", "체험"])
    private String transport;          // 교통 수단 (예: "대중교통", "자차")
}
