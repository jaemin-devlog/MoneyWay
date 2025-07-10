package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.PlaceSearchResultDto;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final RestaurantJejuRepository restaurantRepo;
    private final TourPlaceRepository tourPlaceRepo;

    /**
     * 키워드를 사용하여 모든 유형의 장소를 통합 검색합니다.
     */
    @Transactional(readOnly = true)
    public List<PlaceSearchResultDto> searchPlacesByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        Stream<PlaceSearchResultDto> restaurantResults = restaurantRepo.searchByKeyword(keyword).stream()
                .map(this::toDto);

        Stream<PlaceSearchResultDto> tourPlaceResults = tourPlaceRepo.searchByKeyword(keyword).stream()
                .map(this::toDto);

        return Stream.concat(restaurantResults, tourPlaceResults).toList();
    }

    /**
     * 음식 카테고리 코드로 제주 맛집 목록을 검색합니다.
     */
    @Transactional(readOnly = true)
    public List<PlaceSearchResultDto> searchRestaurantsByCategory(String categoryCode) {
        return restaurantRepo.findByCategoryCode(categoryCode).stream()
                .map(this::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<PlaceSearchResultDto> searchTourPlacesByCategory(String areacode, String cat1) {
        return tourPlaceRepo.findByAreacodeAndCat1(areacode, cat1).stream()
                .map(this::toDto)
                .toList();
    }

    private PlaceSearchResultDto toDto(RestaurantJeju restaurant) {
        return PlaceSearchResultDto.builder()
                .placeId(restaurant.getId())
                .type(PlaceType.JEJU_RESTAURANT)
                .title(restaurant.getTitle())
                .address(restaurant.getAddress())
                .tel(restaurant.getTel())
                .imageUrl(null)
                .build();
    }

    private PlaceSearchResultDto toDto(TourPlace place) {
        return PlaceSearchResultDto.builder()
                .placeId(place.getId())
                .type(PlaceType.TOUR_API)
                .title(place.getTitle())
                .address(place.getAddress())
                .tel(place.getTel())
                .imageUrl(place.getFirstimage())
                .build();
    }
}