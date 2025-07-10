// 📦 PlaceType.java
package com.example.moneyway.place.domain;

/**
 * ✅ 장소 유형 Enum
 * - 다형성 구조에서는 실제 타입을 판별하기 위해 사용
 * - API 응답, 필터링, 저장 시 type 정보로 활용 가능
 */
public enum PlaceType {
    TOUR_API("TourAPI 장소"),
    JEJU_RESTAURANT("제주 맛집");

    private final String description;

    PlaceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
