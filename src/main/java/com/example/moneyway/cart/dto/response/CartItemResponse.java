package com.example.moneyway.cart.dto.response;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

/**
 * 장바구니에 담긴 개별 항목을 나타내는 응답 DTO
 * - 사용자 장바구니 내 하나의 장소 항목을 표현
 * - 프론트에 리스트로 반환될 때 사용됨
 */
@Getter
@Builder
public class CartItemResponse {

    private final Long cartId;        // 장바구니 항목의 고유 ID
    private final Long placeId;       // 담긴 장소의 ID
    private final String placeName;   // 장소 이름
    private final String address;     // 장소 주소
    private final String imageUrl;    // 장소 대표 이미지 URL
    private final int price;          // 유저가 해당 장소에 설정한 금액
    private final String category;    // ✅ [추가] 장소 카테고리

    /**
     * Cart 엔티티를 CartItemResponse로 변환하는 정적 팩토리 메서드
     *
     * @param cart 장바구니 엔티티
     * @return CartItemResponse 객체
     *
     * - Place 엔티티의 주요 정보(이름, 주소, 이미지)와 Cart의 가격 정보를 조합해 응답을 구성함
     * - 프론트에서 장소별 장바구니 항목을 리스트 형태로 보여줄 때 사용됨
     */
    public static CartItemResponse from(Cart cart) {
        Place place = cart.getPlace();
        // ✅ [추가] 카테고리 정보가 null일 경우를 대비하여 안전하게 처리
        String categoryName = place.getCategory() != null ? place.getCategory().getDisplayName() : "기타";

        return CartItemResponse.builder()
                .cartId(cart.getId())                      // 장바구니 고유 ID
                .placeId(place.getId())                    // 장소 ID
                .placeName(place.getPlaceName())           // 장소 이름
                .address(place.getAddress())               // 장소 주소
                .imageUrl(place.getThumbnailUrl())         // 장소 썸네일 이미지 URL
                .price(cart.getPrice())                    // 설정된 가격
                .category(categoryName)                    // ✅ [추가] 카테고리 정보 추가
                .build();
    }
}
