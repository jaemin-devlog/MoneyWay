package com.example.moneyway.cart.domain;

import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 사용자는 하나의 장바구니만 가짐
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // CartItem의 생명주기를 완전히 관리
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CartItem> cartItems = new ArrayList<>();

    private Cart(User user) {
        this.user = user;
    }

    // 정적 팩토리 메서드
    public static Cart from(User user) {
        return new Cart(user);
    }

    // 외부 수정 불가능한 리스트 반환
    public List<CartItem> getCartItems() {
        return Collections.unmodifiableList(cartItems);
    }

    // 기존 장소가 있다면 수량 증가, 없다면 새로 추가
    public void addOrUpdateItem(CartItem newItem) {
        for (CartItem existingItem : this.cartItems) {
            if (existingItem.getPlace().equals(newItem.getPlace())) {
                existingItem.addQuantity(newItem.getQuantity());
                return;
            }
        }
        this.cartItems.add(newItem);
        newItem.setCart(this);
    }

    // 개별 아이템 삭제
    public void removeItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    // 전체 장바구니 비우기
    public void clear() {
        for (CartItem item : new ArrayList<>(this.cartItems)) {
            this.removeItem(item);
        }
    }

    // 전체 장바구니 금액 합계 반환
    public int getTotalPrice() {
        return cartItems.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return this.id != null && this.id.equals(cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
