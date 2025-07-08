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
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int saveCount = 0;

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                Cell idCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (idCell == null) continue;

                String contentId = switch (idCell.getCellType()) {
                    case NUMERIC -> String.valueOf((long) idCell.getNumericCellValue());
                    case STRING -> idCell.getStringCellValue().trim();
                    default -> "";
                };

                if (contentId.isEmpty()) continue;

                Cell priceCell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String priceStr = priceCell.toString().trim();
                if (priceStr.isEmpty()) {
                    System.out.println(" 가격 정보 없음 → contentId: " + contentId);
                    continue;
                }

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

        } catch (Exception e) {
            // 여기서 구체적인 에러 메시지를 로깅하면 Render 로그로 확인 가능
            System.err.println("엑셀 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 처리 실패", e);
        }
    }

}
