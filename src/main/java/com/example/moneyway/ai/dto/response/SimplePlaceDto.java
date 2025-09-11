package com.example.moneyway.ai.dto.response;

public record SimplePlaceDto(
        Long placeId,
        String title,
        String priceInfo,
        String categoryName,
        Double latitude,
        Double longitude
) {}
