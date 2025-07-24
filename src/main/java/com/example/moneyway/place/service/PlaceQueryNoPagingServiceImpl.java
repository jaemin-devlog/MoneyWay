package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;
import com.example.moneyway.place.repository.PlaceRepositoryNoPaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceQueryNoPagingServiceImpl implements PlaceQueryNoPagingService {

    private final PlaceRepositoryNoPaging placeRepositoryNoPaging;

    @Override
    public List<PlaceInfoResponseDto> findPlacesByCategoryNoPaging(PlaceCategory category) {
        List<Place> places;
        if (category == null) {
            places = placeRepositoryNoPaging.findAll();
        } else {
            places = placeRepositoryNoPaging.findByCategory(category);
        }
        return places.stream().map(PlaceInfoResponseDto::from).toList();
    }

    @Override
    public List<PlaceInfoResponseDto> searchPlacesByKeywordNoPaging(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return placeRepositoryNoPaging.findAll().stream()
                    .map(PlaceInfoResponseDto::from).toList();
        } else {
            return placeRepositoryNoPaging.searchByKeyword(keyword).stream()
                    .map(PlaceInfoResponseDto::from).toList();
        }
    }
}
