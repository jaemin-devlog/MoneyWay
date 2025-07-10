// ExcelUploadResult.java (새 파일 또는 RestaurantExcelLoader 내부에 static으로 선언)
package com.example.moneyway.place.dto;

import java.util.List;

/**
 * 엑셀 업로드 처리 결과를 담는 Record
 */
public record ExcelUploadResult(
        int totalCount, //총 시도 건수
        int successCount, //성공 건수
        int skippedCount, //중복으로 인해 건너뛴 건수
        List<String> skippedRows //중복된 행의 제목 목록
) {}