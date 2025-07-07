package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExcelPlacePriceLoader {

    private final TourPlaceRepository tourPlaceRepository;

    public void loadPricesFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int saveCount = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;


                Cell idCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (idCell == null) continue;

                String contentId;
                if (idCell.getCellType() == CellType.NUMERIC) {
                    contentId = String.valueOf((long) idCell.getNumericCellValue());
                } else {
                    contentId = idCell.toString().trim();
                }

                if (contentId.isEmpty()) continue;


                Cell priceCell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String priceStr = priceCell.toString().trim();

                if (priceStr.isEmpty()) {
                    System.out.println(" 가격 정보 없음 → contentId: " + contentId);
                    continue;
                }

                // DB 저장
                Optional<TourPlace> optPlace = tourPlaceRepository.findByContentid(contentId);
                if (optPlace.isPresent()) {
                    TourPlace place = optPlace.get();
                    place.setPrice2(priceStr);
                    tourPlaceRepository.save(place);
                    saveCount++;
                    System.out.println(" 저장 완료 → contentId: " + contentId + ", price2: " + priceStr);
                } else {
                    System.out.println(" DB에 존재하지 않음 → contentId: " + contentId);
                }
            }

            System.out.println(" 총 저장 개수: " + saveCount);

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 읽기 실패", e);
        }
    }
}
