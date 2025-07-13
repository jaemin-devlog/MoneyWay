package com.example.moneyway.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 엑셀 업로드 처리 결과를 담는 불변(Immutable) DTO
 */
@Schema(description = "엑셀 업로드 처리 결과")
public record ExcelUploadResult(
        @Schema(description = "총 시도 건수", example = "100")
        int totalCount,

        @Schema(description = "DB 저장 성공 건수", example = "95")
        int successCount,

        @Schema(description = "중복으로 인해 건너뛴 건수", example = "5")
        int skippedCount,

        @Schema(description = "중복으로 인해 건너뛴 행의 식별자 목록")
        List<String> skippedRows
) {}