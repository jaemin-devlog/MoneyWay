package com.example.moneyway.place.service;

import com.example.moneyway.common.exception.CustomException.CustomFileException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.internal.TourApiResponseDto;
import com.example.moneyway.place.dto.response.ExcelUploadResult;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import com.example.moneyway.place.repository.TourPlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 모든 데이터 '관리' (생성, 수정, 동기화, 업로드) 로직을 통합 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminDataService {

    private static final String HEADER_NAME = "이름";
    private static final String HEADER_ADDRESS = "주소";
    private static final String HEADER_SCORE = "점수";
    private static final String HEADER_REVIEW_COUNT = "리뷰수";
    private static final String HEADER_TEL = "전화번호";
    private static final String HEADER_MENU = "메뉴";
    private static final String HEADER_URL = "url";

    private static final int TOUR_API_FETCH_SIZE = 100;

    private final TourApiClient tourApiClient;
    private final TourPlaceRepository tourPlaceRepository;
    private final RestaurantJejuRepository restaurantRepository;
    private final ObjectMapper objectMapper;
    private final TourUpdateHelper tourUpdateHelper;

    public void syncAllTourData() {
        int pageNo = 1;
        int totalCount = 0;
        int totalSavedCount = 0;

        do {
            try {
                // TourApiClient는 내부적으로 TOUR_API_FETCH_SIZE(100개)만큼 데이터를 가져옵니다.
                String jsonResponse = tourApiClient.getTourListInJeju(pageNo);
                if (jsonResponse == null) break;

                TourApiResponseDto responseDto = objectMapper.readValue(jsonResponse, TourApiResponseDto.class);
                TourApiResponseDto.Body body = responseDto.getResponse().getBody();

                if (pageNo == 1) totalCount = body.getTotalCount();

                List<TourApiResponseDto.Item> items = body.getItems().getItem();
                if (items == null || items.isEmpty()) break;

                Set<String> contentIdsFromApi = items.stream().map(TourApiResponseDto.Item::getContentid).collect(Collectors.toSet());
                Set<String> existingContentIds = tourPlaceRepository.findContentidsIn(contentIdsFromApi);

                List<TourPlace> newPlaces = items.stream()
                        .filter(item -> !existingContentIds.contains(item.getContentid()))
                        .map(this::mapItemToTourPlace)
                        .collect(Collectors.toList());

                if (!newPlaces.isEmpty()) {
                    tourPlaceRepository.saveAll(newPlaces);
                    totalSavedCount += newPlaces.size();
                    log.info("페이지 {}: 새로운 관광지 {}건 저장 완료.", pageNo, newPlaces.size());
                }
                pageNo++;
            } catch (Exception e) {
                log.error("페이지 {} 처리 중 오류 발생. 동기화를 중단합니다.", pageNo, e);
                break;
            }
            // ✅ [수정] 반복문 종료 조건을 실제 API 호출 크기인 TOUR_API_FETCH_SIZE(100)로 수정
        } while ((pageNo - 1) * TOUR_API_FETCH_SIZE < totalCount);

        log.info("TourAPI 전체 데이터 동기화 완료. 총 저장된 새 관광지: {}건", totalSavedCount);
    }

    public void syncAllTourDetails() {
        log.info("장소 상세 정보 동기화 작업을 시작합니다.");
        Page<TourPlace> placePage;
        int pageNum = 0;
        do {
            placePage = tourPlaceRepository.findAll(PageRequest.of(pageNum, 500)); // DB 조회는 500개씩 처리
            placePage.getContent().forEach(tourUpdateHelper::updatePlaceDetail);
            log.info("상세 정보 동기화 진행... ({} 페이지 완료)", pageNum + 1);
            pageNum++;
        } while (placePage.hasNext());
        log.info("장소 상세 정보 동기화 완료.");
    }

    public void syncAllTourIntros() {
        log.info("장소 소개 정보 동기화 작업을 시작합니다.");
        Page<TourPlace> placePage;
        int pageNum = 0;
        do {
            placePage = tourPlaceRepository.findAll(PageRequest.of(pageNum, 500)); // DB 조회는 500개씩 처리
            placePage.getContent().forEach(tourUpdateHelper::updatePlaceIntro);
            log.info("소개 정보 동기화 진행... ({} 페이지 완료)", pageNum + 1);
            pageNum++;
        } while (placePage.hasNext());
        log.info("장소 소개 정보 동기화 완료.");
    }

    public ExcelUploadResult loadRestaurantsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomFileException(ErrorCode.FILE_IS_EMPTY);
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnIndexMap = findExcelColumnIndices(sheet.getRow(0), HEADER_NAME, HEADER_ADDRESS);

            Map<String, RestaurantJeju> candidatesFromExcel = new LinkedHashMap<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String title = getStringValue(row, columnIndexMap, HEADER_NAME);
                String address = getStringValue(row, columnIndexMap, HEADER_ADDRESS);

                if (title.isBlank() || address.isBlank()) continue;

                String uniqueKey = title + "||" + address;
                candidatesFromExcel.computeIfAbsent(uniqueKey, k -> buildRestaurantFromRow(row, columnIndexMap));
            }

            if (candidatesFromExcel.isEmpty()) {
                return new ExcelUploadResult(0, 0, 0, Collections.emptyList());
            }

            Set<String> existingRestaurantKeys = new HashSet<>();
            List<String> candidateKeys = new ArrayList<>(candidatesFromExcel.keySet());
            int batchSize = 1000;

            for (int i = 0; i < candidateKeys.size(); i += batchSize) {
                List<String> batch = candidateKeys.subList(i, Math.min(i + batchSize, candidateKeys.size()));
                existingRestaurantKeys.addAll(restaurantRepository.findExistingUniqueKeys(batch));
            }

            List<RestaurantJeju> restaurantsToSave = candidatesFromExcel.keySet().stream()
                    .filter(key -> !existingRestaurantKeys.contains(key))
                    .map(candidatesFromExcel::get)
                    .collect(Collectors.toList());

            if (!restaurantsToSave.isEmpty()) {
                restaurantRepository.saveAll(restaurantsToSave);
            }

            int totalCount = candidatesFromExcel.size();
            int successCount = restaurantsToSave.size();
            int skippedCount = totalCount - successCount;
            List<String> skippedRows = existingRestaurantKeys.stream()
                    .map(key -> key.replace("||", " "))
                    .collect(Collectors.toList());

            return new ExcelUploadResult(totalCount, successCount, skippedCount, skippedRows);

        } catch (IOException e) {
            log.error("맛집 엑셀 파일 처리 중 I/O 오류 발생", e);
            throw new CustomFileException(ErrorCode.FILE_PROCESSING_ERROR, e);
        } catch (IllegalArgumentException e) {
            log.error("엑셀 파일 처리 중 유효하지 않은 인자: {}", e.getMessage());
            throw new CustomFileException(ErrorCode.INVALID_FILE_FORMAT, e);
        }
    }

    public void loadPricesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어있습니다.");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Map<String, String> priceUpdateMap = parsePriceExcelToMap(workbook);
            if (priceUpdateMap.isEmpty()) return;

            List<TourPlace> placesInDb = tourPlaceRepository.findByContentidIn(priceUpdateMap.keySet());

            for (TourPlace place : placesInDb) {
                String newPrice = priceUpdateMap.get(place.getContentid());
                if (newPrice != null) {
                    place.setPrice2(newPrice);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("가격 정보 엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    private TourPlace mapItemToTourPlace(TourApiResponseDto.Item item) {
        return TourPlace.builder()
                .title(item.getTitle())
                .tel(item.getTel())
                .contentid(item.getContentid())
                .contenttypeid(item.getContenttypeid())
                .addr1(item.getAddr1())
                .firstimage(item.getFirstimage())
                .firstimage2(item.getFirstimage2())
                .areacode(item.getAreacode())
                .sigungucode(item.getSigungucode())
                .cat1(item.getCat1())
                .cat2(item.getCat2())
                .cat3(item.getCat3())
                .mapx(item.getMapx())
                .mapy(item.getMapy())
                .mlevel(item.getMlevel())
                .createdtime(item.getCreatedtime())
                .modifiedtime(item.getModifiedtime())
                .build();
    }

    private Map<String, String> parsePriceExcelToMap(Workbook workbook) {
        Map<String, String> priceData = new HashMap<>();
        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> columnIndexMap = findExcelColumnIndices(sheet.getRow(0), "contentid", "price2");
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String contentId = getStringValue(row, columnIndexMap, "contentid");
            String price = getStringValue(row, columnIndexMap, "price2");
            if (!contentId.isEmpty()) {
                priceData.put(contentId, price);
            }
        }
        return priceData;
    }

    private RestaurantJeju buildRestaurantFromRow(Row row, Map<String, Integer> columnIndexMap) {
        return RestaurantJeju.builder()
                .title(getStringValue(row, columnIndexMap, HEADER_NAME))
                .tel(getStringValue(row, columnIndexMap, HEADER_TEL))
                .address(getStringValue(row, columnIndexMap, HEADER_ADDRESS))
                .score(getStringValue(row, columnIndexMap, HEADER_SCORE))
                .review(getStringValue(row, columnIndexMap, HEADER_REVIEW_COUNT))
                .menu(getStringValue(row, columnIndexMap, HEADER_MENU))
                .url(getStringValue(row, columnIndexMap, HEADER_URL))
                .build();
    }

    private Map<String, Integer> findExcelColumnIndices(Row headerRow, String... requiredHeaders) {
        if (headerRow == null) throw new IllegalArgumentException("엑셀 파일에 헤더 행이 존재하지 않습니다.");
        Map<String, Integer> columnIndexMap = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        for (Cell cell : headerRow) {
            String headerValue = formatter.formatCellValue(cell).toLowerCase().trim();
            columnIndexMap.put(headerValue, cell.getColumnIndex());
        }
        if (!Stream.of(requiredHeaders).map(String::toLowerCase).allMatch(columnIndexMap::containsKey)) {
            throw new IllegalArgumentException("엑셀 파일에 필수 헤더 " + Arrays.toString(requiredHeaders) + "가(이) 존재하지 않습니다.");
        }
        return columnIndexMap;
    }

    private String getStringValue(Row row, Map<String, Integer> columnIndexMap, String columnName) {
        Integer index = columnIndexMap.get(columnName.toLowerCase());
        if (index == null) return "";
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}