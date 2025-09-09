package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장소 목록 및 검색 결과 응답 DTO (경량)")
public record PlaceInfoResponseDto(
        @Schema(description = "장소 ID", example = "1")
        Long placeId,

        @Schema(description = "장소 이름", example = "성산일출봉")
        String title,

        @Schema(description = "주소", example = "제주특별자치도 서귀포시 성산읍 일출로 284-12")
        String address,

        @Schema(description = "썸네일 이미지 URL", example = "http://tong.visitkorea.or.kr/cms/resource/50/2667850_image2_1.jpg")
        String thumbnailUrl,

        @Schema(description = "카테고리 표시 이름", example = "관광지")
        String categoryName,

        @Schema(description = "가격 정보 문자열", example = "입장료 5000원")
        String priceInfo,

        @Schema(description = "위도 (Y좌표)", example = "33.458023")
        Double latitude,

        @Schema(description = "경도 (X좌표)", example = "126.942653")
        Double longitude,

        // ✅ 추가 필드
        @Schema(description = "평점", example = "4.5")
        Double rating,

        @Schema(description = "대표 리뷰", example = "경치가 너무 좋아요!")
        String topReview
) {
    public static PlaceInfoResponseDto from(Place place) {
        Double rating = null;
        String topReview = null;

        if (place instanceof TourPlace tour) {
            rating = tour.getRating();
            topReview = tour.getTopReview();
        } else if (place instanceof RestaurantJeju restaurant) {
            rating = restaurant.getRating();
            topReview = restaurant.getTopReview();
        }

        return new PlaceInfoResponseDto(
                place.getId(),
                place.getTitle(),
                place.getAddress(),
                place.getThumbnailUrl(),
                place.getCategory().getDisplayName(),
                place.getDisplayPrice(),
                parseDouble(place.getMapY()), // 위도
                parseDouble(place.getMapX()), // 경도
                rating,
                topReview
        );
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
