package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.RestaurantJeju;
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

    private Long placeId;
    private String title;
    private String score;
    private String review;
    private String address;
    private String tel;
    private String menu;
    private String url;
    private String categoryCode;

    /**
     * ✅ [추가] 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * 이 메서드가 없어서 컴파일 오류가 발생했습니다.
     */
    public static GetJejuRestaurantDto from(RestaurantJeju entity) {
        return GetJejuRestaurantDto.builder()
                .placeId(entity.getId())
                .title(entity.getTitle())
                .score(entity.getScore())
                .review(entity.getReview())
                .address(entity.getAddress())
                .tel(entity.getTel())
                .menu(entity.getMenu())
                .url(entity.getUrl())
                .categoryCode(entity.getCategoryCode())
                .build();
    }
}