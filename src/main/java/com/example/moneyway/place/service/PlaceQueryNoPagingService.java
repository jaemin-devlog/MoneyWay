package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;

import java.util.List;

public interface PlaceQueryNoPagingService {
    List<PlaceInfoResponseDto> findPlacesByCategoryNoPaging(PlaceCategory category);
    List<PlaceInfoResponseDto> searchPlacesByKeywordNoPaging(String keyword);
}
