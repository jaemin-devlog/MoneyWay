package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class RestaurantExcelLoader {

    private final RestaurantJejuRepository repository;

    public void loadRestaurantsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int saveCount = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String title = getStringValue(row.getCell(0));
                String score = getStringValue(row.getCell(1));
                String review = getStringValue(row.getCell(2));
                String address = getStringValue(row.getCell(3));
                String tel = getStringValue(row.getCell(4));
                String menu = getStringValue(row.getCell(5));
                String url = getStringValue(row.getCell(6));
                String categoryCode = getStringValue(row.getCell(7));

                if (title.isBlank()) continue;

                RestaurantJeju entity = new RestaurantJeju(
                        title, score, review, address, tel, menu, url, categoryCode
                );

                repository.save(entity);
                saveCount++;
            }

            System.out.println("총 저장 개수: " + saveCount);

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 처리 실패", e);
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
