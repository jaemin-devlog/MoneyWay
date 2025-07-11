package com.example.moneyway.place.controller;

import com.example.moneyway.common.exception.CustomException.CustomFileException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.place.dto.response.ExcelUploadResult;
import com.example.moneyway.place.service.AdminDataService; // [수정] 통합된 서비스 주입
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Admin Data Management", description = "데이터 동기화 및 업로드 API")
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDataController {

    // ✅ [수정] 5개의 서비스 의존성이 단 하나로 통합되었습니다.
    private final AdminDataService adminDataService;

    @Operation(summary = "TourAPI 전체 데이터 동기화")
    @PostMapping("/sync/tour-all")
    public ResponseEntity<String> syncAllTourData() {
        // [수정] adminDataService 호출
        adminDataService.syncAllTourData();
        return ResponseEntity.ok("제주 관광 데이터를 DB에 저장했습니다.");
    }

    @Operation(summary = "TourAPI 소개 정보 동기화")
    @PostMapping("/sync/tour-intro")
    public ResponseEntity<String> syncAllTourIntros() {
        // [수정] adminDataService 호출 및 메서드명 일관성 통일
        adminDataService.syncAllTourIntros();
        return ResponseEntity.ok("소개 정보가 저장되었습니다.");
    }

    @Operation(summary = "TourAPI 상세 정보 동기화")
    @PostMapping("/sync/tour-detail")
    public ResponseEntity<String> syncAllTourDetails() {
        // [수정] adminDataService 호출 및 메서드명 일관성 통일
        adminDataService.syncAllTourDetails();
        return ResponseEntity.ok("상세 정보가 저장되었습니다.");
    }

    @Operation(summary = "관광지 가격 정보 엑셀 업로드")
    @PostMapping("/upload/tour-prices")
    public ResponseEntity<String> uploadTourPrices(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomFileException(ErrorCode.FILE_IS_EMPTY);
        }
        // [수정] adminDataService 호출
        adminDataService.loadPricesFromExcel(file);
        return ResponseEntity.ok("가격 정보 업데이트를 성공적으로 완료했습니다.");
    }

    @Operation(summary = "다이닝코드 정보 엑셀 업로드")
    @PostMapping("/upload/restaurants")
    // ✅ [개선] 단순 문자열 대신, 처리 결과를 담은 객체를 반환하여 클라이언트에게 더 유용한 정보를 제공합니다.
    public ResponseEntity<ExcelUploadResult> uploadRestaurantData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomFileException(ErrorCode.FILE_IS_EMPTY);
        }
        // [수정] adminDataService 호출
        ExcelUploadResult result = adminDataService.loadRestaurantsFromExcel(file);
        return ResponseEntity.ok(result);
    }
}