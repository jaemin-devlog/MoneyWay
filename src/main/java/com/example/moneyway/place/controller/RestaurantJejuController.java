package com.example.moneyway.place.controller;

import com.example.moneyway.place.dto.RestaurantJeju;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants-jeju")
public class RestaurantJejuController {

    private final RestaurantJejuRepository repository;

    public RestaurantJejuController(RestaurantJejuRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RestaurantJeju> getRestaurants(@RequestParam(required = false) String category) {
        if (category != null) {
            return repository.findByCategoryCode(category);
        }
        return repository.findAll();
    }
}
