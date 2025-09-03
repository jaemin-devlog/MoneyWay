package com.example.moneyway.ai.dto.response;

import java.time.LocalTime;

public record PlaceDto(
        Long placeId,
        String title,
        String address,
        String thumbnailUrl,
        String categoryName,
        String priceInfo,
        Double latitude,
        Double longitude,
        String time,        // 오전, 점심, 숙소 ...
        int cost,
        String startTime,
        String endTime
) {}
