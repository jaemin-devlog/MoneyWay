package com.example.moneyway.cart.domain;

import com.example.moneyway.place.domain.RestaurantJeju;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 아이템이 속한 장바구니
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 장바구니에 담긴 장소 (RestaurantJeju와 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private RestaurantJeju place;

    @Builder
    public CartItem(Cart cart, RestaurantJeju place) {
        this.cart = cart;
        this.place = place;
    }
}