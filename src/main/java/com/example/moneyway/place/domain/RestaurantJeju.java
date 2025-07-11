package com.example.moneyway.place.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ✅ Place 엔티티를 상속받는 맛집 엔티티.
 * @Table 어노테이션을 제거하여 JPA의 JOINED 상속 전략을 따르도록 합니다.
 * 이제 이 엔티티는 'restaurant_jeju' 테이블과 매핑됩니다.
 */
@Entity
@Getter
@SuperBuilder // ✅ 상속 관계 빌더
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED) // ✅ SuperBuilder가 사용할 생성자
@PrimaryKeyJoinColumn(name = "place_pk_id")
// ✅ @Table 어노테이션이 제거되어 JPA 상속 전략을 따름
public class RestaurantJeju extends Place {

    private String address;
    private String score;
    private String review;
    private String menu;
    private String url;
    private String img;
    private String tel;
    private String price2;
    private String categoryCode;

    /**
     * ✅ Place 추상 메서드 구현
     * - RestaurantJeju의 주소는 'address' 필드를 사용합니다.
     */
    @Override
    public String getAddress() {
        return this.address;
    }
}