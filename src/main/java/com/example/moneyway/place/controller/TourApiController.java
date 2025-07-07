package com.example.moneyway.place.controller;

import com.example.moneyway.place.service.ExcelPlacePriceLoader;
import com.example.moneyway.place.service.TourApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Tour API", description = "여행 계획 관련 API")
@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourApiController {

    private final TourApiService tourApiService;
    private final ExcelPlacePriceLoader excelPlacePriceLoader;

    @Operation(summary = "제주도 데이터 저장", description = "제주도 데이터를 저장합니다.")
    @GetMapping("/JEJU")
    public ResponseEntity<String> syncAllData() {
        tourApiService.syncAllTourData();
        return ResponseEntity.ok(" 제주 관광 데이터를 DB에 저장했습니다.");
    }

    @PostMapping("/admin/update-price")
    public ResponseEntity<String> updatePlacePrices(@RequestParam("file") MultipartFile file) {
        excelPlacePriceLoader.loadPricesFromExcel(file);
        return ResponseEntity.ok("가격 정보 업데이트 완료");
    }
}
