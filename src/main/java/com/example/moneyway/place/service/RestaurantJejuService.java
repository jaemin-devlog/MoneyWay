package com.example.moneyway.place.service;

import com.example.moneyway.place.dto.GetJejuRestaurantDto;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantJejuService {

    private final RestaurantJejuRepository repository;

//    public List<GetJejuRestaurantDto> findByCategoryCode(String categoryCode) {
//        return repository.findByCategoryCode(categoryCode).stream()
//                .map(r -> GetJejuRestaurantDto.builder()
//                        .title(r.getTitle())
//                        .score(r.getScore())
//                        .review(r.getReview())
//                        .address(r.getAddress())
//                        .tel(r.getTel())
//                        .menu(r.getMenu())
//                        .url(r.getUrl())
//                        .categoryCode(r.getCategoryCode())
//                        .build())
//                .collect(Collectors.toList());
//    }

    public List<GetJejuRestaurantDto> getAllRestaurants() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public List<GetJejuRestaurantDto> getByCategoryCode(String categoryCode) {
        return repository.findByCategoryCode(categoryCode).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private GetJejuRestaurantDto convertToDto(RestaurantJeju entity) {
        return GetJejuRestaurantDto.builder()
                .title(entity.getTitle())
                .score(entity.getScore())
                .review(entity.getReview())
                .address(entity.getAddress())
                .tel(entity.getTel())
                .menu(entity.getMenu())
                .url(entity.getUrl())
                .categoryCode(entity.getCategoryCode())
                .build();
    }
}
