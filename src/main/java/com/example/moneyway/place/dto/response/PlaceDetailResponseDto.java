package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장소 상세 정보 응답 DTO")
public record PlaceDetailResponseDto(
        @Schema(description = "장소 ID", example = "1")
        Long placeId,

        @Schema(description = "장소 이름", example = "성산일출봉")
        String title,

        @Schema(description = "주소", example = "제주특별자치도 서귀포시 성산읍 일출로 284-12")
        String address,

        @Schema(description = "장소 이미지 URL 목록")
        List<String> imageUrls,

        @Schema(description = "카테고리 표시 이름", example = "관광지")
        String categoryName,

        @Schema(description = "가격 정보 문자열", example = "입장료 5000원")
        String priceInfo,

        @Schema(description = "장소 상세 설명 (HTML)", example = "성산일출봉은...")
        String description,

        @Schema(description = "메뉴 정보", example = "흑돼지 2인분, 해물라면")
        String menu,

        // ✅ [개선] 위도와 경도 필드 추가
        @Schema(description = "위도 (Y좌표)", example = "33.458023")
        Double latitude,

        @Schema(description = "경도 (X좌표)", example = "126.942653")
        Double longitude
) {
    public static PlaceDetailResponseDto from(Place place) {
        return new PlaceDetailResponseDto(
                place.getId(),
                place.getTitle(),
                place.getAddress(),
                place.getImageUrls(),
                place.getCategory().getDisplayName(),
                place.getDisplayPrice(),
                place.getDescription(), // TourPlace의 경우 infotext가 여기에 매핑됩니다.
                place.getMenu(),
                parseDouble(place.getMapY()), // 위도 = mapY
                parseDouble(place.getMapX())  // 경도 = mapX
        );
    }

    // ✅ [개선] 문자열 좌표를 Double로 안전하게 변환하는 헬퍼 메서드
    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null; // 변환 실패 시 null 반환
        }
    }
}