package com.example.moneyway.place.dto;

import com.example.moneyway.place.domain.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 장소 통합 검색 결과 DTO
 * - TourPlace, RestaurantJeju 등 모든 장소의 검색 결과를 표현
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchResultDto {

    private Long placeId; //장소 PK(장바구니, 상세조회)
    private PlaceType type; //장소 유형
    private String title;// 장소명
    private String address; //주소
    private String tel;// 전화번호
    private String imageUrl; //대표 이미지 URL (있을 경우)
}