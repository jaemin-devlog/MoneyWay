package com.example.moneyway.cart.dto.response;

import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.example.moneyway.cart.domain.Cart;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CartResponseDto {

    private final Map<String, List<CartItemResponseDto>> groupedItems;
    private final int cartTotalPrice;

    private CartResponseDto(Cart cart) {
        this.groupedItems = cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .collect(Collectors.groupingBy(CartItemResponseDto::getCategoryDisplayName));

        this.cartTotalPrice = cart.getTotalPrice();
    }

    public static CartResponseDto from(Cart cart) {
        return new CartResponseDto(cart);
    }
}