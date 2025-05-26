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

    @PostMapping("/sync-seogwipo")
    public ResponseEntity<String> syncSeogwipo() {
        tourApiService.syncTourDataToDatabase();
        return ResponseEntity.ok("📥 Seogwipo 관광 정보가 DB에 저장되었습니다.");
    }
    @GetMapping("/jeju/all")
    public ResponseEntity<String> syncJejuData() {
        tourApiService.syncJejuTourData();
        return ResponseEntity.ok("✅ 제주 전체 관광 데이터를 DB에 저장했습니다.");
    }
}
