package com.example.moneyway.place.domain;

public record AiPlaceDto(
        Long placeId,
        String title,
        String address,
        String categoryName,
        String priceInfo,
        String latitude,   // 위도
        String longitude,  // 경도
        String time,
        int cost,
        String startTime,
        String endTime
) {

}
