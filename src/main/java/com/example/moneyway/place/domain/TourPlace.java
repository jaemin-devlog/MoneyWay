package com.example.moneyway.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter // TourUpdateHelper에서 필드를 업데이트하기 위해 필요
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "place_pk_id")
@Table(name = "tour_place")
public class TourPlace extends Place {

    @Column(unique = true, nullable = false)
    private String contentid;
    private String contenttypeid;
    private String createdtime;
    private String modifiedtime;
    @Column
    private String addr1;
    private String areacode;
    @Column
    private String mapx;
    @Column
    private String mapy;
    @Column
    private String firstimage;
    @Column
    private String firstimage2;
    private String cat1;
    private String cat2;
    private String cat3;
    private String mlevel;
    private String sigungucode;

    private String priceInfo;

    @Column(columnDefinition = "TEXT")
    private String infotext;

    @Override
    protected PlaceCategory calculateCategory() {
        if (this.contenttypeid == null) {
            return PlaceCategory.TOURIST_ATTRACTION;
        }
        return switch (this.contenttypeid) {
            case "32" -> PlaceCategory.ACCOMMODATION;
            case "38" -> PlaceCategory.SHOPPING;
            case "28" -> PlaceCategory.ACTIVITY;
            case "12", "14", "15" -> PlaceCategory.TOURIST_ATTRACTION;
            default -> null;
        };
    }

    @Override
    public String getDisplayPrice() {
        if (this.priceInfo == null || this.priceInfo.isBlank()) {
            if (PlaceCategory.ACCOMMODATION.equals(getCategory())) {
                return "가격 문의";
            }
            return "무료";
        }

        return switch (getCategory()) {
            case ACCOMMODATION -> this.priceInfo + "원";
            case ACTIVITY, SHOPPING, TOURIST_ATTRACTION -> "입장료 " + this.priceInfo + "원";
            default -> this.priceInfo + "원";
        };
    }

    @Override
    public int getNumericPrice() {
        if (this.priceInfo == null || this.priceInfo.isBlank() || !this.priceInfo.matches("\\d+")) {
            return 0;
        }
        return Integer.parseInt(this.priceInfo);
    }

    @Override
    public String getAddress() {
        return this.addr1;
    }

    @Override
    public String getThumbnailUrl() {
        if (this.firstimage != null && !this.firstimage.isBlank()) {
            return this.firstimage;
        }
        return this.firstimage2;
    }

    @Override
    public List<String> getImageUrls() {
        return Stream.of(this.firstimage, this.firstimage2)
                .filter(Objects::nonNull)
                .filter(url -> !url.isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return this.infotext;
    }

    @Override
    public String getMenu() {
        return null;
    }

    public void updatePriceInfo(String newPriceInfo) {
        this.priceInfo = newPriceInfo;
    }

    @Override
    public String getMapX() {
        return this.mapx;
    }

    @Override
    public String getMapY() {
        return this.mapy;
    }

}