package com.example.moneyway.cart.dto.response;

import lombok.Getter;

import java.util.List;

/**
 * 사용자 장바구니 전체 정보를 담는 응답 DTO
 */
@Getter
public class CartResponse {
    private final List<CartItemResponse> cartItems;
    private final int totalCount;
    private final long totalPrice;

    /**
     * 장바구니 항목 리스트를 받아 전체 정보(총 개수, 총액)를 계산하여 생성합니다.
     */
    public CartResponse(List<CartItemResponse> cartItems) {
        this.cartItems = cartItems;
        this.totalCount = cartItems.size();
        this.totalPrice = cartItems.stream().mapToLong(CartItemResponse::getPrice).sum();
    }
}