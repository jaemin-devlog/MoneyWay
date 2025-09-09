package com.example.moneyway.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "place_pk_id")
public class RestaurantJeju extends Place {

    @Column(name = "address")
    private String address;
    private String score;
    private String review;
    private String menu;
    private String url;

    @Column(name = "img")
    private String thumbnailUrl;

    private String priceInfo;

    private String categoryCode;

    @Column(name = "mapx")
    private String mapX;
    @Column(name = "mapy")
    private String mapY;

    // 추가된 필드
    @Column(name = "rating")
    private Double rating;   // 별점 (예: 4.3)

    @Column(name = "top_review", columnDefinition = "TEXT")
    private String topReview; // 대표 리뷰 텍스트

    @Override
    protected PlaceCategory calculateCategory() {
        if ("c2".equals(this.categoryCode)) {
            return PlaceCategory.CAFE;
        }
        return PlaceCategory.RESTAURANT;
    }

    @Override
    public String getDisplayPrice() {
        if (this.priceInfo == null || this.priceInfo.isBlank()) {
            return "가격 정보 없음";
        }
        String prefix = PlaceCategory.CAFE.equals(getCategory()) ? "약 " : "평균 ";
        return prefix + this.priceInfo + "원";
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public int getNumericPrice() {
        if (this.priceInfo == null || this.priceInfo.isBlank() || !this.priceInfo.matches("\\d+")) {
            return 0;
        }
        return Integer.parseInt(this.priceInfo);
    }

    @Override
    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    @Override
    @Transient
    public List<String> getImageUrls() {
        if (this.thumbnailUrl == null || this.thumbnailUrl.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(this.thumbnailUrl);
    }

    @Override
    public String getDescription() {
        return this.topReview; // 리뷰를 설명처럼 노출 가능
    }

    @Override
    public String getMapX() {
        return this.mapX;
    }

    @Override
    public String getMapY() {
        return this.mapY;
    }

    @Override
    public String getMenu() {
        return this.menu;
    }
}
