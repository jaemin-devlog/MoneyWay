package com.example.moneyway.place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_pk_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    private String tel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PlaceCategory category;

    @PrePersist
    @PreUpdate
    public void updateCategory() {
        this.category = calculateCategory();
    }

    protected abstract PlaceCategory calculateCategory();

    public abstract String getDisplayPrice();

    public abstract int getNumericPrice();

    public abstract String getAddress();

    public abstract String getThumbnailUrl();

    public abstract List<String> getImageUrls();

    public abstract String getDescription();

    public abstract String getMenu();

    public abstract String getMapX(); // 경도(Longitude)

    public abstract String getMapY(); // 위도(Latitude)
    /**
     * 장소의 이름을 반환하는 공통 메서드.
     * 모든 Place 자식 클래스에서 title 필드를 이름으로 사용합니다.
     */
    public String getPlaceName() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place place)) return false;
        return Objects.equals(this.id, place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}