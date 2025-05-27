package com.example.moneyway.plan.controller;

import com.example.moneyway.plan.service.TourApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourApiController {

    private final TourApiService tourApiService;

    @GetMapping("/all")
    public ResponseEntity<String> syncAllData() {
        tourApiService.syncAllTourData();
        return ResponseEntity.ok("✅ 전국 관광 데이터를 DB에 저장했습니다.");
    }
}
