package com.example.moneyway.place.domain;

public record AiPlaceDto(
        Long placeId,
        String title,
        String categoryName,
        String priceInfo,
        String time,
        int cost,
        String startTime,
        String endTime
) {}
