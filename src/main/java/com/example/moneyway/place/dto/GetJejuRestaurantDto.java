package com.example.moneyway.place.dto;

import lombok.*;

/**
 * ✅ 제주 맛집 상세 정보 DTO
 * - RestaurantJeju 엔티티의 데이터를 클라이언트로 전달하는 역할
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJejuRestaurantDto {

    private Long placeId;       // 시스템 내부 고유 ID
    private String title;       // 맛집 이름
    private String score;       // 평점
    private String review;      // 대표 리뷰 또는 리뷰 수
    private String address;     // 주소
    private String tel;         // 전화번호
    private String menu;        // 대표 메뉴 정보 (텍스트)
    private String url;         // 상세 정보 URL (예: 다이닝)
    private String categoryCode;// 음식 카테고리
}