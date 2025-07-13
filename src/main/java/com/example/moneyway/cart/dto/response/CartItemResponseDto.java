package com.example.moneyway.cart.dto.response;

import com.example.moneyway.cart.domain.CartItem;
import com.example.moneyway.place.domain.Place;
import lombok.Getter;

@Getter
public class CartItemResponseDto {

    private final Long cartItemId; // 장바구니 아이템 ID (수정/삭제 시 필요)
    private final Long placeId;    // 장소 ID
    private final String title;    // 장소 이름
    private final String imageUrl; // 장소 대표 이미지 URL
    private final int quantity;    // 수량
    private final int price;       // 개당 가격 (스냅샷)
    private final int totalPrice;  // 항목별 총 가격 (quantity * price)
    private final String categoryDisplayName;

    private CartItemResponseDto(CartItem cartItem) {
        Place place = cartItem.getPlace(); // 가독성을 위해 변수로 추출

        this.cartItemId = cartItem.getId();
        this.placeId = place.getId();
        this.title = place.getTitle();
        this.imageUrl = place.getThumbnailUrl();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
        this.totalPrice = cartItem.getTotalPrice();
        this.categoryDisplayName = place.getCategory().getDisplayName();
    }

    // 정적 팩토리 메서드: 엔티티를 DTO로 변환하는 로직을 캡슐화
    public static CartItemResponseDto from(CartItem cartItem) {
        return new CartItemResponseDto(cartItem);
    }
}