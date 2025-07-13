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
        String menu
) {
    /**
     * Place 엔티티를 상세 DTO로 변환하는 정적 팩토리 메서드입니다.
     * 다형성을 통해 Place 객체가 스스로 올바른 데이터를 제공합니다.
     */
    public static PlaceDetailResponseDto from(Place place) {
        return new PlaceDetailResponseDto(
                place.getId(),
                place.getTitle(),
                place.getAddress(),
                place.getImageUrls(),
                place.getCategory().getDisplayName(),
                place.getDisplayPrice(),
                place.getDescription(),
                place.getMenu()
        );
    }
}