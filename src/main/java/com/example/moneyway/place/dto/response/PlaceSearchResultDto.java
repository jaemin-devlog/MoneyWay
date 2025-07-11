package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import java.util.Objects;

/**
 * 장소 검색 결과를 담는 불변(Immutable) 데이터 전송 객체(DTO).
 * Java Record를 사용하여 보일러플레이트 코드를 최소화합니다.
 *
 * @param placeId  장소의 고유 ID
 * @param type     장소의 유형 (JEJU_RESTAURANT, TOUR_API 등)
 * @param title    장소명
 * @param address  주소
 * @param tel      전화번호
 * @param imageUrl 대표 이미지 URL
 */
public record PlaceSearchResultDto(
        Long placeId,
        PlaceType type,
        String title,
        String address,
        String tel,
        String imageUrl,
        String img,
        String price2,
        String categoryCode,
        String mapx,
        String mapy

) {

    // --- 정적 팩토리 메서드 ---

    /**
     * ✅ [수정] 모든 Place 하위 타입에 대한 DTO 변환을 처리하는 범용 메서드입니다.
     * instanceof를 사용하여 실제 타입에 맞는 private from 메서드를 호출합니다.
     * @param place 변환할 Place 엔티티 (null이 아니어야 함)
     * @return 생성된 PlaceSearchResultDto 객체
     */
    public static PlaceSearchResultDto from(Place place) {
        Objects.requireNonNull(place, "Place entity cannot be null");

        if (place instanceof RestaurantJeju restaurant) {
            return from(restaurant);
        } else if (place instanceof TourPlace tourPlace) {
            return from(tourPlace);
        } else {
            // 지원하지 않는 타입에 대한 예외 처리
            throw new IllegalArgumentException("Unsupported Place subtype: " + place.getClass().getName());
        }
    }

    /**
     * RestaurantJeju 엔티티로부터 DTO를 생성합니다.
     * ✅ [수정] 외부에서는 범용 from(Place) 메서드만 사용하도록 private으로 변경합니다.
     * @param restaurant 변환할 RestaurantJeju 엔티티
     * @return 생성된 PlaceSearchResultDto 객체
     */
    private static PlaceSearchResultDto from(RestaurantJeju restaurant) {
        return new PlaceSearchResultDto(
                restaurant.getId(),
                PlaceType.JEJU_RESTAURANT,
                restaurant.getTitle(),
                restaurant.getAddress(),
                restaurant.getTel(),
                restaurant.getImg(),
                null,
                restaurant.getPrice2(),
                restaurant.getCategoryCode(),
                null,
                null




        );
    }

    /**
     * TourPlace 엔티티로부터 DTO를 생성합니다.
     * ✅ [수정] 외부에서는 범용 from(Place) 메서드만 사용하도록 private으로 변경합니다.
     * @param place 변환할 TourPlace 엔티티
     * @return 생성된 PlaceSearchResultDto 객체
     */
    private static PlaceSearchResultDto from(TourPlace place) {
        return new PlaceSearchResultDto(
                place.getId(),
                PlaceType.TOUR_API,
                place.getTitle(),
                place.getAddr1(),
                place.getTel(),
                place.getFirstimage(),
                place.getFirstimage(),
                place.getPrice2(),
                place.getCat1(),
                String.valueOf(place.getMapx()),
                String.valueOf(place.getMapy())
        );
    }


}