package com.example.moneyway.cart.domain;

import com.example.moneyway.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_item_cart_place", // (cart_id, place_id) 복합 유니크
                columnNames = {"cart_id", "place_id"}
        )
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cart N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Place N:1 (JEJU_RESTAURANT, TOUR_PLACE 등 상속 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // 수량 (최소 1 이상)
    @Column(nullable = false)
    private int quantity;

    // 장소 가격 스냅샷 (단위: 원)
    @Column(nullable = false)
    private int price;

    @Builder
    public CartItem(Cart cart, Place place, int quantity, int price) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        if (price < 0) throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        this.cart = cart;
        this.place = place;
        this.quantity = quantity;
        this.price = price;
    }

    // 수량 추가
    public void addQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("추가 수량은 0 이상이어야 합니다.");
        this.quantity += quantity;
    }

    // 수량 변경
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        this.quantity = newQuantity;
    }

    protected void setCart(Cart cart) {
        this.cart = cart;
    }

    // 개별 항목의 총 가격 반환
    public int getTotalPrice() {
        return this.quantity * this.price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return this.id != null && this.id.equals(cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + (cart != null ? cart.getId() : "null") +
                ", placeId=" + (place != null ? place.getId() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
