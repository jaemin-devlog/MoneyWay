// 📦 RestaurantJeju.java
package com.example.moneyway.place.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 제주도 맛집 데이터를 담는 엔티티.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "place_pk_id")
@Table(name = "jeju_restaurants")
public class RestaurantJeju extends Place {

    @Column(nullable = false, length = 255)
    private String address;

    private String score;
    private String review;
    private String menu;
    private String url;
    private String categoryCode;

    @Builder
    public RestaurantJeju(String title, String tel, String address,
                          String score, String review, String menu,
                          String url, String categoryCode) {
        super(title, tel);

        this.address = address; // address 필드 초기화
        this.score = score;
        this.review = review;
        this.menu = menu;
        this.url = url;
        this.categoryCode = categoryCode;
    }

    /**
     * [추가] 부모의 추상 메서드를 구현합니다.
     * RestaurantJeju의 주소는 'address' 필드를 사용합니다.
     */
    @Override
    public String getAddress() {
        return this.address;
    }
}