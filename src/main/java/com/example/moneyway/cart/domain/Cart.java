package com.example.moneyway.cart.domain;

import com.example.moneyway.common.domain.BaseTimeEntity;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.user.domain.User;
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
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_user_place",
                        columnNames = {"user_id", "place_id"}
                )
        }
)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private int price;

    @Builder
    private Cart(User user, Place place, int price) {
        this.user = user;
        this.place = place;
        this.price = price;
    }

    public static Cart of(User user, Place place, int price) {
        Objects.requireNonNull(user, "User는 null일 수 없습니다.");
        Objects.requireNonNull(place, "Place는 null일 수 없습니다.");

        return Cart.builder()
                .user(user)
                .place(place)
                .price(price)
                .build();
    }

    /**
     * ✅ [추가] 장바구니에 담긴 항목의 가격을 수정하는 메서드
     * @param newPrice 새로운 가격
     */
    public void updatePrice(int newPrice) {
        if (newPrice < 0) {
            // 가격은 0 이상이어야 한다는 비즈니스 규칙을 엔티티 레벨에서 검증
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.price = newPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return this.id != null && Objects.equals(this.id, cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}