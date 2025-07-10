package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.dto.ExcelUploadResult;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantExcelLoader {

    private final RestaurantJejuRepository repository;

    @Transactional
    public ExcelUploadResult loadRestaurantsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        Set<String> existingRestaurants = repository.findAll().stream()
                .map(r -> r.getTitle() + "||" + r.getAddress())
                .collect(Collectors.toSet());

        List<RestaurantJeju> restaurantsToSave = new ArrayList<>();
        List<String> skippedRows = new ArrayList<>();
        int totalCount = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnIndexMap = findColumnIndices(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String title = getStringValue(row.getCell(columnIndexMap.get("title")));
                String address = getStringValue(row.getCell(columnIndexMap.get("address")));

                // 고유 키 생성에 필요한 필수 값(title, address)을 함께 검증
                if (title.isBlank() || address.isBlank()) {
                    // 로그 메시지를 더 명확하게 수정
                    log.warn("필수 값(title 또는 address)이 비어있어 엑셀 {}행을 건너뜁니다.", i + 1);
                    continue;
                }

                totalCount++;

                String uniqueKey = title + "||" + address;

                if (existingRestaurants.contains(uniqueKey)) {
                    String skippedMessage = String.format("%s (%s, 엑셀 %d행)", title, address, i + 1);
                    skippedRows.add(skippedMessage);
                    log.warn("중복된 데이터 발견, 건너뜁니다: {}", skippedMessage);
                    continue;
                }

                // 엔티티 생성 로직을 별도 메서드로 분리하여 가독성 향상
                RestaurantJeju entity = buildRestaurantFromRow(row, columnIndexMap, title, address);

                restaurantsToSave.add(entity);
                existingRestaurants.add(uniqueKey);
            }

            if (!restaurantsToSave.isEmpty()) {
                repository.saveAll(restaurantsToSave);
            }

            log.info("엑셀 처리 완료. 총 시도: {}, 성공: {}, 중복 제외: {}", totalCount, restaurantsToSave.size(), skippedRows.size());
            return new ExcelUploadResult(totalCount, restaurantsToSave.size(), skippedRows.size(), skippedRows);

        } catch (IOException e) {
            log.error("엑셀 파일 처리 중 오류가 발생했습니다.", e);
            throw new RuntimeException("엑셀 파일 처리 실패", e);
        }
    }

    /**
     * 엑셀의 한 행(Row)에서 데이터를 읽어 RestaurantJeju 엔티티를 생성합니다.
     *
     * @param row          데이터를 읽을 행
     * @param columnIndexMap 헤더와 인덱스 정보
     * @param title        이미 읽은 제목
     * @param address      이미 읽은 주소
     * @return 생성된 RestaurantJeju 엔티티
     */
    private RestaurantJeju buildRestaurantFromRow(Row row, Map<String, Integer> columnIndexMap, String title, String address) {
        return RestaurantJeju.builder()
                .title(title)
                .address(address)
                .score(getStringValue(row.getCell(columnIndexMap.get("score"))))
                .review(getStringValue(row.getCell(columnIndexMap.get("review"))))
                .tel(getStringValue(row.getCell(columnIndexMap.get("tel"))))
                .menu(getStringValue(row.getCell(columnIndexMap.get("menu"))))
                .url(getStringValue(row.getCell(columnIndexMap.get("url"))))
                .categoryCode(getStringValue(row.getCell(columnIndexMap.get("categorycode"))))
                .build();
    }

    private Map<String, Integer> findColumnIndices(Row headerRow) {
        if (headerRow == null) {
            throw new IllegalArgumentException("엑셀 파일에 헤더 행이 존재하지 않습니다.");
        }
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerValue = getStringValue(cell).toLowerCase();
            columnIndexMap.put(headerValue, cell.getColumnIndex());
        }

        boolean hasRequiredHeaders = Stream.of("title", "address").allMatch(columnIndexMap::containsKey);
        if (!hasRequiredHeaders) {
            throw new IllegalArgumentException("엑셀 파일에 필수 헤더(title, address)가 존재하지 않습니다.");
        }
        return columnIndexMap;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}