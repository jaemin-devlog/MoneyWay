package com.example.moneyway.place.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {
    RESTAURANT("맛집"),
    CAFE("카페"),
    ACCOMMODATION("숙소"),
    TOURIST_ATTRACTION("관광지"),
    ACTIVITY("액티비티/체험"),
    SHOPPING("쇼핑");

    private final String displayName;
}