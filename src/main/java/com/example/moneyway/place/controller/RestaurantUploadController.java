package com.example.moneyway.place.controller;

import com.example.moneyway.place.service.RestaurantExcelLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantUploadController {

    private final RestaurantExcelLoader restaurantExcelLoader;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadRestaurantExcel(@RequestParam("file") MultipartFile file) {
        restaurantExcelLoader.loadRestaurantsFromExcel(file);
        return ResponseEntity.ok("음식점 데이터 업로드 완료");
    }
}
