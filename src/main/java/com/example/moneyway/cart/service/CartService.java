package com.example.moneyway.cart.service;

import com.example.moneyway.cart.dto.request.AddCartRequest;
import com.example.moneyway.cart.dto.request.UpdateCartPriceRequest;
import com.example.moneyway.cart.dto.response.CartResponse;

public interface CartService {

    /**
     * 사용자의 장바구니에 장소를 추가합니다.
     */
    void addPlaceToCart(Long userId, AddCartRequest request);

    /**
     * 사용자의 장바구니 정보를 조회합니다.
     */
    CartResponse getCart(Long userId);

    /**
     * 장바구니에 담긴 특정 항목의 가격을 수정합니다.
     */
    void updateCartItemPrice(Long userId, Long cartId, UpdateCartPriceRequest request);

    /**
     * 장바구니에서 특정 항목을 삭제합니다.
     */
    void removeCartItem(Long userId, Long cartId);
}