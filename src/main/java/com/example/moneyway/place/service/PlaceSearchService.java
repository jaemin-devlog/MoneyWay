package com.example.moneyway.place.service;

import com.example.moneyway.place.dto.PlaceSearchResultDto;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final RestaurantJejuRepository restaurantRepo;
    private final TourPlaceRepository tourPlaceRepo;

    public List<PlaceSearchResultDto> searchPlacesByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        List<PlaceSearchResultDto> restaurantResults = restaurantRepo.findAll().stream()
                .filter(r -> stringContains(r.getTitle(), lowerKeyword)
                        || stringContains(r.getMenu(), lowerKeyword))
                .map(r -> new PlaceSearchResultDto("RESTAURANT", r.getTitle(), r.getAddress(), r.getTel(), r.getUrl()))
                .collect(Collectors.toList());

        List<PlaceSearchResultDto> tourPlaceResults = tourPlaceRepo.findAll().stream()
                .filter(p -> stringContains(p.getTitle(), lowerKeyword)
                        || stringContains(p.getOverview(), lowerKeyword)
                        || stringContains(p.getInfotext(), lowerKeyword)
                        || stringContains(p.getSubname(), lowerKeyword))
                .map(p -> new PlaceSearchResultDto("TOUR_PLACE", p.getTitle(), p.getAddr1(), p.getTel(), null))
                .collect(Collectors.toList());

        restaurantResults.addAll(tourPlaceResults);
        return restaurantResults;
    }

    private boolean stringContains(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword);
    }

}
