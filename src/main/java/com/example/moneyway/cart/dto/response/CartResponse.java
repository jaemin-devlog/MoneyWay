package com.example.moneyway.cart.dto;

import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CartResponse {

    /**
     * 장바구니 전체를 보여주는 응답 DTO
     * 항목 리스트와 총 예상 비용을 포함한다.
     */
    @Getter
    @Builder
    public static class CartViewDto {
        private List<CartItemDto> items;
        private int totalEstimatedCost;
    }

    /**
     * 장바구니의 개별 항목을 나타내는 DTO
     */
    @Getter
    public static class CartItemDto {
        private Long cartItemId;
        private String placeId;
        private PlaceType placeType;
        private String title;
        private String address;
        private String imageUrl;
        private int estimatedCost; // 사용자가 입력한 비용
        private String memo;       // 사용자가 입력한 메모

        @Builder
        private CartItemDto(Long cartItemId, String placeId, PlaceType placeType, String title, String address, String imageUrl, int estimatedCost, String memo) {
            this.cartItemId = cartItemId;
            this.placeId = placeId;
            this.placeType = placeType;
            this.title = title;
            this.address = address;
            this.imageUrl = imageUrl;
            this.estimatedCost = estimatedCost;
            this.memo = memo;
        }

        // TourPlace 정보로 DTO 생성
        public static CartItemDto from(com.example.moneyway.cart.domain.CartItem cartItem, TourPlace place) {
            return CartItemDto.builder()
                    .cartItemId(cartItem.getId())
                    .placeId(place.getContentid())
                    .placeType(PlaceType.TOUR_API)
                    .title(place.getTitle())
                    .address(place.getAddr1())
                    .imageUrl(place.getFirstimage())
                    .estimatedCost(cartItem.getEstimatedCost())
                    .memo(cartItem.getMemo())
                    .build();
        }

        // RestaurantJeju 정보로 DTO 생성
        public static CartItemDto from(com.example.moneyway.cart.domain.CartItem cartItem, RestaurantJeju place) {
            return CartItemDto.builder()
                    .cartItemId(cartItem.getId())
                    .placeId(place.getTitle())
                    .placeType(PlaceType.JEJU_RESTAURANT)
                    .title(place.getTitle())
                    .address(place.getAddress())
                    .imageUrl(null) // 제주 맛집은 대표 이미지가 없음
                    .estimatedCost(cartItem.getEstimatedCost())
                    .memo(cartItem.getMemo())
                    .build();
        }
    }
}