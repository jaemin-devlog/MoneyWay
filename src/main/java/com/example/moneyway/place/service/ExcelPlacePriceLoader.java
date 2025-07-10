package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelPlacePriceLoader {

    private final TourPlaceRepository tourPlaceRepository;

    // 1. 찾으려는 헤더 이름을 상수로 정의
    private static final String CONTENT_ID_HEADER = "contentid";
    private static final String PRICE_HEADER = "price2";

    @Transactional
    public void loadPricesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 엑셀 파싱 로직을 개선하여 동적 인덱싱 적용
        Map<String, String> priceUpdates = parsePriceDataFromExcel(file);

        if (priceUpdates.isEmpty()) {
            log.info("엑셀에서 업데이트할 유효한 가격 정보를 찾지 못했습니다.");
            return;
        }

        List<TourPlace> placesInDb = tourPlaceRepository.findByContentidIn(priceUpdates.keySet());

        Set<String> foundContentIds = placesInDb.stream()
                .map(TourPlace::getContentid)
                .collect(Collectors.toSet());

        priceUpdates.keySet().stream()
                .filter(id -> !foundContentIds.contains(id))
                .forEach(id -> log.warn("DB에 존재하지 않는 contentId: {}", id));

        for (TourPlace place : placesInDb) {
            String newPrice = priceUpdates.get(place.getContentid());
            place.setPrice2(newPrice);
        }

        log.info("총 {}개의 장소 가격 정보를 성공적으로 업데이트했습니다.", placesInDb.size());
    }

    private Map<String, String> parsePriceDataFromExcel(MultipartFile file) {
        Map<String, String> priceData = new HashMap<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 첫 번째 행(헤더)을 읽어 각 컬럼의 인덱스를 찾음
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("엑셀 파일에 헤더 행이 존재하지 않습니다.");
            }
            Map<String, Integer> columnIndexMap = findColumnIndices(headerRow);

            // 3. 데이터 행(두 번째 행부터)을 순회하며 처리
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 4. 저장된 인덱스를 사용하여 셀 데이터를 가져옴
                String contentId = getCellStringValue(row.getCell(columnIndexMap.get(CONTENT_ID_HEADER)));
                if (contentId.isEmpty()) continue;

                String priceStr = getCellStringValue(row.getCell(columnIndexMap.get(PRICE_HEADER)));
                if (priceStr.isEmpty()) {
                    log.warn("가격 정보 없음 -> contentId: {}", contentId);
                    continue;
                }
                priceData.put(contentId, priceStr);
            }
        } catch (Exception e) {
            log.error("엑셀 파일 처리 중 심각한 오류 발생", e);
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
        return priceData;
    }

    /**
     * 헤더 행을 분석하여 필요한 컬럼들의 인덱스를 찾아 Map으로 반환합니다.
     * @param headerRow 엑셀의 첫 번째 행 (헤더)
     * @return {"헤더이름": 인덱스} 형태의 Map
     */
    private Map<String, Integer> findColumnIndices(Row headerRow) {
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerValue = getCellStringValue(cell).toLowerCase(); // 소문자로 변환하여 비교
            if (headerValue.equals(CONTENT_ID_HEADER)) {
                columnIndexMap.put(CONTENT_ID_HEADER, cell.getColumnIndex());
            } else if (headerValue.equals(PRICE_HEADER)) {
                columnIndexMap.put(PRICE_HEADER, cell.getColumnIndex());
            }
        }

        // 필수 컬럼이 모두 존재하는지 확인
        if (!columnIndexMap.containsKey(CONTENT_ID_HEADER)) {
            throw new IllegalArgumentException("엑셀 파일에서 '" + CONTENT_ID_HEADER + "' 컬럼을 찾을 수 없습니다.");
        }
        if (!columnIndexMap.containsKey(PRICE_HEADER)) {
            throw new IllegalArgumentException("엑셀 파일에서 '" + PRICE_HEADER + "' 컬럼을 찾을 수 없습니다.");
        }

        return columnIndexMap;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue().trim();
            default -> cell.toString().trim();
        };
    }
}