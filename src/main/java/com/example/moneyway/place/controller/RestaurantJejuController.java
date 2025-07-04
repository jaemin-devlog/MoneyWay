package com.example.moneyway.place.controller;

import com.example.moneyway.place.dto.GetJejuRestaurantDto;
import com.example.moneyway.place.service.RestaurantJejuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantJejuController {

    private final RestaurantJejuService restaurantJejuService;

    @GetMapping
    public ResponseEntity<List<GetJejuRestaurantDto>> getRestaurantsByCategory(
            @RequestParam(value = "categoryCode", required = false) String categoryCode) {

        List<GetJejuRestaurantDto> result;

        if (categoryCode == null || categoryCode.isBlank()) {
            result = restaurantJejuService.getAllRestaurants();
        } else {
            result = restaurantJejuService.getByCategoryCode(categoryCode);
        }

        return ResponseEntity.ok(result);
    }
}
