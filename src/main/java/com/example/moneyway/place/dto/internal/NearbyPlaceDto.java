package com.example.moneyway.place.dto.internal;

public interface NearbyPlaceDto {
    Long getId();
    String getTitle();
    String getCategoryName();
    String getAddress();
    String getThumbnailUrl();   // firstimage
    String getThumbnailUrl2();  // firstimage2
    String getPriceInfo();
    Double getMapx();
    Double getMapy();
    Double getDistance();       // distance 계산 결과도 SELECT 함
}
