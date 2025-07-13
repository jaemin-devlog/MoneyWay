package com.example.moneyway.place.controller;

import com.example.moneyway.place.dto.response.ExcelUploadResult;
import com.example.moneyway.place.service.AdminDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Admin Data Management", description = "데이터 동기화 및 업로드 API (관리자용)")
// @SecurityRequirement(name = "bearerAuth") // ✅ [수정] 인증 요구사항 제거
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDataController {

    private final AdminDataService adminDataService;
    private static final List<String> ALLOWED_EXCEL_TYPES = List.of(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "application/vnd.ms-excel"  // .xls
    );

    /**
     * API의 모든 성공 응답을 위한 표준 래퍼(Wrapper) DTO입니다.
     * Record를 사용하여 불변(Immutable)하고 간결하게 정의했습니다.
     */
    @Getter
    @Schema(description = "API 성공 응답 래퍼")
    public static class SuccessResponse<T> {
        @Schema(description = "응답 메시지", example = "작업이 성공적으로 시작되었습니다.")
        private final String message;

        @Schema(description = "응답 데이터 (없을 경우 null)")
        private final T data;

        private SuccessResponse(String message, T data) {
            this.message = message;
            this.data = data;
        }

        // 데이터가 없는 성공 응답을 위한 정적 팩토리 메서드
        public static SuccessResponse<Void> withMessage(String message) {
            return new SuccessResponse<>(message, null);
        }

        // 데이터가 있는 성공 응답을 위한 정적 팩토리 메서드
        public static <T> SuccessResponse<T> withData(String message, T data) {
            return new SuccessResponse<>(message, data);
        }
    }

    @Operation(summary = "TourAPI 전체 데이터 동기화", description = "백그라운드에서 TourAPI의 모든 장소 정보를 가져와 DB에 저장합니다. (시간 소요)")
    @ApiResponse(responseCode = "200", description = "동기화 작업이 성공적으로 시작됨",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    @PostMapping("/sync/tour-all")
    public ResponseEntity<SuccessResponse<Void>> syncAllTourData() {
        // ✅ 서비스 계층의 해당 메서드는 @Async로 비동기 처리되어야 합니다.
        adminDataService.syncAllTourData();
        return ResponseEntity.ok(SuccessResponse.withMessage("TourAPI 전체 데이터 동기화 작업이 시작되었습니다. 완료까지 시간이 소요될 수 있습니다."));
    }

    @Operation(summary = "TourAPI 상세 정보 동기화", description = "백그라운드에서 모든 관광지의 상세 정보를 업데이트합니다. (시간 소요)")
    @ApiResponse(responseCode = "200", description = "동기화 작업이 성공적으로 시작됨",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    @PostMapping("/sync/tour-detail")
    public ResponseEntity<SuccessResponse<Void>> syncAllTourDetails() {
        adminDataService.syncAllTourDetails();
        return ResponseEntity.ok(SuccessResponse.withMessage("TourAPI 상세 정보 동기화 작업이 시작되었습니다."));
    }

    @Operation(summary = "맛집 정보 엑셀 업로드", description = "엑셀 파일을 업로드하여 맛집 정보를 DB에 저장합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 및 처리 성공",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    @ApiResponse(responseCode = "400", description = "파일이 비어있거나, 허용되지 않는 파일 형식인 경우", content = @Content)
    @PostMapping("/upload/restaurants")
    public ResponseEntity<SuccessResponse<ExcelUploadResult>> uploadRestaurantData(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        ExcelUploadResult result = adminDataService.loadRestaurantsFromExcel(file);
        return ResponseEntity.ok(SuccessResponse.withData("파일이 성공적으로 처리되었습니다.", result));
    }
    @Operation(summary = "관광지 가격 정보 엑셀 업로드", description = "엑셀 파일을 업로드하여 기존 관광지의 가격 정보를 업데이트합니다. (contentId 기준)")
    @ApiResponse(responseCode = "200", description = "업로드 및 업데이트 성공",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class)))
    @ApiResponse(responseCode = "400", description = "파일이 비어있거나, 허용되지 않는 파일 형식인 경우", content = @Content)
    @PostMapping("/upload/tour-prices")
    public ResponseEntity<SuccessResponse<ExcelUploadResult>> uploadTourPlacePrices(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        // 서비스에 새로운 메서드를 호출합니다.
        ExcelUploadResult result = adminDataService.updateTourPlacePricesFromExcel(file);
        return ResponseEntity.ok(SuccessResponse.withData("관광지 가격 정보가 성공적으로 업데이트되었습니다.", result));
    }
    /**
     * 업로드된 파일의 유효성을 검증하는 private 헬퍼 메서드
     * - 파일 존재 여부와 허용된 MIME 타입인지 확인합니다.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (!ALLOWED_EXCEL_TYPES.contains(contentType)) {
            log.warn("허용되지 않은 파일 타입 업로드 시도: {}", contentType);
            throw new IllegalArgumentException("엑셀 파일(.xls, .xlsx)만 업로드할 수 있습니다.");
        }
    }
}