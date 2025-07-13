package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.Place;
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
        String priceInfo
) {
    /**
     * Place 엔티티를 목록용 DTO로 변환하는 정적 팩토리 메서드입니다.
     */
    public static PlaceInfoResponseDto from(Place place) {
        return new PlaceInfoResponseDto(
                place.getId(),
                place.getTitle(),
                place.getAddress(),
                place.getThumbnailUrl(),
                place.getCategory().getDisplayName(),
                place.getDisplayPrice()
        );
    }
}