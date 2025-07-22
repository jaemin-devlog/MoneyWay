package com.example.moneyway.place.service;

import com.example.moneyway.common.exception.CustomException.CustomFileException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.internal.TourApiResponseDto;
import com.example.moneyway.place.dto.response.ExcelUploadResult;
import com.example.moneyway.place.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ✅ [최종본] 모든 데이터 '관리' 로직을 통합 관리하는 서비스
 * - 병렬 처리 및 공통 로직 추상화를 통해 성능, 가독성, 유지보수성을 극대화했습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminDataService {

    // --- 상수 선언: 엔티티 필드명과 일관성을 갖도록 수정 ---
    private static final String HEADER_TITLE = "title";
    private static final String HEADER_ADDRESS = "address";
    private static final String HEADER_SCORE = "score";
    private static final String HEADER_REVIEW = "review_count";
    private static final String HEADER_TEL = "tel";
    private static final String HEADER_MENU = "menu";
    private static final String HEADER_URL = "url";
    private static final String HEADER_PRICE_INFO = "price2";
    private static final String HEADER_THUMBNAIL_URL = "img";
    private static final String HEADER_CATEGORY_CODE = "category_code";
    private static final String HEADER_MAP_X = "mapx"; // 맛집/카페 위경도용
    private static final String HEADER_MAP_Y = "mapy"; //  맛집/카페 위경도용
    private static final String HEADER_CONTENT_ID = "contentid"; //  관광지 가격 업데이트용 헤더
    private static final int TOUR_API_FETCH_SIZE = 100;
    private static final int DB_QUERY_BATCH_SIZE = 1000;

    // --- 의존성 주입 ---
    private final PlaceRepository placeRepository;
    private final TourApiClient tourApiClient;
    private final ObjectMapper objectMapper;
    private final TourUpdateHelper tourUpdateHelper;
    private final ExecutorService dataSyncTaskExecutor;

    @Async("dataSyncTaskExecutor")
    public void syncAllTourData() {
        log.info("TourAPI 전체 데이터 동기화 작업을 시작합니다...");
        long startTime = System.currentTimeMillis();
        int pageNo = 1;
        int totalCount = 0;
        AtomicInteger totalSavedCount = new AtomicInteger(0);

        do {
            try {
                String jsonResponse = tourApiClient.getTourListInJeju(pageNo);
                if (jsonResponse == null) break;

                TourApiResponseDto responseDto = objectMapper.readValue(jsonResponse, TourApiResponseDto.class);
                TourApiResponseDto.Body body = responseDto.response().body();

                if (pageNo == 1) totalCount = body.totalCount();

                List<TourApiResponseDto.Item> items = body.items().item().stream()
                        .filter(item -> List.of("12", "14", "15", "28", "32", "38").contains(item.contenttypeid()))
                        .toList();


                List<String> contentIdsFromApi = items.stream().map(TourApiResponseDto.Item::contentid).toList();
                Set<String> existingContentIds = placeRepository.findExistingContentIds(contentIdsFromApi);

                List<TourPlace> newPlaces = items.stream()
                        .filter(item -> !existingContentIds.contains(item.contentid()))
                        .map(this::mapItemToTourPlace)
                        .collect(Collectors.toList());

                if (!newPlaces.isEmpty()) {
                    placeRepository.saveAll(newPlaces);
                    totalSavedCount.addAndGet(newPlaces.size());
                    log.info("페이지 {}: 새로운 관광지 {}건 저장 완료.", pageNo, newPlaces.size());
                }
                pageNo++;
            } catch (Exception e) {
                log.error("페이지 {} 처리 중 오류 발생. 동기화를 중단합니다.", pageNo, e);
                break;
            }
        } while ((pageNo - 1) * TOUR_API_FETCH_SIZE < totalCount && totalCount > 0);

        long endTime = System.currentTimeMillis();
        log.info("TourAPI 전체 데이터 동기화 완료. 총 저장된 새 관광지: {}건 (소요 시간: {}ms)", totalSavedCount.get(), (endTime - startTime));
    }

    public void syncAllTourDetails() {
        syncTourDataInParallel("상세 정보", tourUpdateHelper::updatePlaceDetail);
    }

    public ExcelUploadResult loadRestaurantsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomFileException(ErrorCode.FILE_IS_EMPTY);
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnIndexMap = findExcelColumnIndices(sheet.getRow(0), HEADER_TITLE, HEADER_ADDRESS, HEADER_MAP_X, HEADER_MAP_Y);

            // 1. 엑셀에서 고유한 맛집 후보 목록 읽기
            Map<String, RestaurantJeju> candidatesFromExcel = readUniqueRestaurantsFromSheet(sheet, columnIndexMap);
            if (candidatesFromExcel.isEmpty()) {
                return new ExcelUploadResult(0, 0, 0, Collections.emptyList());
            }

            // 2. DB에 이미 존재하는 맛집 키 목록 조회
            Set<String> existingRestaurantKeys = findExistingRestaurantKeysInBatches(new ArrayList<>(candidatesFromExcel.keySet()));

            // 3. 새로운 맛집만 필터링하여 저장
            List<RestaurantJeju> restaurantsToSave = filterNewRestaurants(candidatesFromExcel, existingRestaurantKeys);
            if (!restaurantsToSave.isEmpty()) {
                placeRepository.saveAll(restaurantsToSave);
            }

            // 4. 최종 결과 리포트 생성
            return buildUploadResult(candidatesFromExcel.size(), restaurantsToSave.size(), existingRestaurantKeys);

        } catch (IOException e) {
            log.error("맛집 엑셀 파일 처리 중 I/O 오류 발생", e);
            throw new CustomFileException(ErrorCode.FILE_PROCESSING_ERROR, e);
        } catch (IllegalArgumentException e) {
            log.error("엑셀 파일 처리 중 유효하지 않은 인자: {}", e.getMessage());
            throw new CustomFileException(ErrorCode.INVALID_FILE_FORMAT, e);
        }
    }

    /**
     * ✅ [추가] 엑셀 파일을 읽어 기존 관광지의 가격 정보를 업데이트합니다.
     *
     * @param file contentid와 priceInfo(price2) 컬럼을 포함하는 엑셀 파일
     * @return 처리 결과 DTO
     */
    public ExcelUploadResult updateTourPlacePricesFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomFileException(ErrorCode.FILE_IS_EMPTY);
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            // 엑셀에 'contentid'와 'price2' 헤더가 있는지 확인
            Map<String, Integer> columnIndexMap = findExcelColumnIndices(sheet.getRow(0), HEADER_CONTENT_ID, HEADER_PRICE_INFO);

            // 1. 엑셀에서 업데이트할 가격 정보 읽기 (contentId -> priceInfo)
            Map<String, String> priceUpdates = readPriceUpdatesFromSheet(sheet, columnIndexMap);
            if (priceUpdates.isEmpty()) {
                return new ExcelUploadResult(0, 0, 0, Collections.emptyList());
            }

            // 2. DB에서 업데이트 대상이 될 TourPlace 엔티티들을 한 번에 조회
            List<TourPlace> placesToUpdate = placeRepository.findTourPlacesByContentIds(priceUpdates.keySet());
            Map<String, TourPlace> placeMap = placesToUpdate.stream()
                    .collect(Collectors.toMap(TourPlace::getContentid, place -> place));

            // 3. 가격 정보 업데이트 및 결과 집계
            AtomicInteger successCount = new AtomicInteger(0);
            List<String> notFoundContentIds = new ArrayList<>();

            priceUpdates.forEach((contentId, newPrice) -> {
                TourPlace place = placeMap.get(contentId);
                if (place != null) {
                    place.updatePriceInfo(newPrice); // 엔티티 내부 메서드로 상태 변경
                    successCount.incrementAndGet();
                } else {
                    notFoundContentIds.add(contentId);
                }
            });

            // 4. 최종 결과 리포트 생성
            // @Transactional에 의해 변경된 엔티티는 자동으로 DB에 반영됨 (save 호출 불필요)
            log.info("관광지 가격 정보 업데이트 완료. 총 요청: {}, 성공: {}, 실패: {}",
                    priceUpdates.size(), successCount.get(), notFoundContentIds.size());

            return new ExcelUploadResult(priceUpdates.size(), successCount.get(), notFoundContentIds.size(), notFoundContentIds);

        } catch (IOException e) {
            log.error("관광지 가격 엑셀 파일 처리 중 I/O 오류 발생", e);
            throw new CustomFileException(ErrorCode.FILE_PROCESSING_ERROR, e);
        } catch (IllegalArgumentException e) {
            log.error("엑셀 파일 처리 중 유효하지 않은 인자: {}", e.getMessage());
            throw new CustomFileException(ErrorCode.INVALID_FILE_FORMAT, e);
        }
    }


    // --- Private Helper Methods ---

    // ... syncTourDataInParallel, readUniqueRestaurantsFromSheet 등 기존 헬퍼 메서드는 변경 없이 그대로 유지 ...
    private void syncTourDataInParallel(String taskName, Consumer<TourPlace> updateAction) {
        log.info("장소 {} 병렬 동기화 작업을 시작합니다.", taskName);
        long startTime = System.currentTimeMillis();

        List<TourPlace> allTourPlaces = placeRepository.findAllTourPlaces();
        if (allTourPlaces.isEmpty()) {
            log.info("{} 동기화 대상이 없습니다.", taskName);
            return;
        }

        final int totalSize = allTourPlaces.size();
        final AtomicInteger progressCount = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = allTourPlaces.stream()
                .map(place -> CompletableFuture.runAsync(() -> {
                    updateAction.accept(place);
                    int currentProgress = progressCount.incrementAndGet();
                    if (currentProgress % 100 == 0 || currentProgress == totalSize) {
                        log.info("[{}] 동기화 진행... ({}/{})", taskName, currentProgress, totalSize);
                    }
                }, dataSyncTaskExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        log.info("장소 {} 병렬 동기화 완료. 총 {}건 처리. (소요 시간: {}ms)", taskName, totalSize, (endTime - startTime));
    }

    private Map<String, RestaurantJeju> readUniqueRestaurantsFromSheet(Sheet sheet, Map<String, Integer> columnIndexMap) {
        Map<String, RestaurantJeju> candidates = new LinkedHashMap<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String title = getStringValue(row, columnIndexMap, HEADER_TITLE);
            String address = getStringValue(row, columnIndexMap, HEADER_ADDRESS);

            if (title.isBlank() || address.isBlank()) continue;

            String uniqueKey = title + "||" + address;
            candidates.computeIfAbsent(uniqueKey, k -> buildRestaurantFromRow(row, columnIndexMap));
        }
        return candidates;
    }

    private Set<String> findExistingRestaurantKeysInBatches(List<String> candidateKeys) {
        Set<String> existingKeys = new HashSet<>();
        for (int i = 0; i < candidateKeys.size(); i += DB_QUERY_BATCH_SIZE) {
            List<String> batch = candidateKeys.subList(i, Math.min(i + DB_QUERY_BATCH_SIZE, candidateKeys.size()));
            existingKeys.addAll(placeRepository.findExistingRestaurantUniqueKeys(batch));
        }
        return existingKeys;
    }

    private List<RestaurantJeju> filterNewRestaurants(Map<String, RestaurantJeju> candidates, Set<String> existingKeys) {
        return candidates.keySet().stream()
                .filter(key -> !existingKeys.contains(key))
                .map(candidates::get)
                .collect(Collectors.toList());
    }

    private ExcelUploadResult buildUploadResult(int totalCount, int successCount, Set<String> skippedKeys) {
        int skippedCount = totalCount - successCount;
        List<String> skippedRows = skippedKeys.stream()
                .map(key -> key.replace("||", " "))
                .collect(Collectors.toList());
        return new ExcelUploadResult(totalCount, successCount, skippedCount, skippedRows);
    }

    /**
     * ✅ [추가] 엑셀 시트에서 contentId와 priceInfo를 읽어 맵으로 반환하는 헬퍼 메서드
     */
    private Map<String, String> readPriceUpdatesFromSheet(Sheet sheet, Map<String, Integer> columnIndexMap) {
        Map<String, String> priceUpdates = new HashMap<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String contentId = getStringValue(row, columnIndexMap, HEADER_CONTENT_ID);
            String priceInfo = getStringValue(row, columnIndexMap, HEADER_PRICE_INFO).replaceAll("[^\\d]", "");

            if (!contentId.isBlank()) {
                priceUpdates.put(contentId, priceInfo);
            }
        }
        return priceUpdates;
    }

    // ... mapItemToTourPlace, buildRestaurantFromRow 등 나머지 헬퍼 메서드는 변경 없이 그대로 유지 ...
    private TourPlace mapItemToTourPlace(TourApiResponseDto.Item item) {
        return TourPlace.builder()
                .title(item.title())
                .tel(item.tel())
                .contentid(item.contentid())
                .contenttypeid(item.contenttypeid())
                .addr1(item.addr1())
                .firstimage(item.firstimage())
                .firstimage2(item.firstimage2())
                .areacode(item.areacode())
                .sigungucode(item.sigungucode())
                .cat1(item.cat1())
                .cat2(item.cat2())
                .cat3(item.cat3())
                .mapx(item.mapx())
                .mapy(item.mapy())
                .mlevel(item.mlevel())
                .createdtime(item.createdtime())
                .modifiedtime(item.modifiedtime())
                .build();
    }

    private RestaurantJeju buildRestaurantFromRow(Row row, Map<String, Integer> columnIndexMap) {
        return RestaurantJeju.builder()
                .title(getStringValue(row, columnIndexMap, HEADER_TITLE))
                .tel(getStringValue(row, columnIndexMap, HEADER_TEL))
                .address(getStringValue(row, columnIndexMap, HEADER_ADDRESS))
                .score(getStringValue(row, columnIndexMap, HEADER_SCORE))
                .review(getStringValue(row, columnIndexMap, HEADER_REVIEW))
                .menu(getStringValue(row, columnIndexMap, HEADER_MENU))
                .url(getStringValue(row, columnIndexMap, HEADER_URL))
                .thumbnailUrl(getStringValue(row, columnIndexMap, HEADER_THUMBNAIL_URL))
                .priceInfo(getStringValue(row, columnIndexMap, HEADER_PRICE_INFO))
                .categoryCode(getStringValue(row, columnIndexMap, HEADER_CATEGORY_CODE))
                .mapX(getStringValue(row, columnIndexMap, HEADER_MAP_X)) // ✅ [추가] mapX 필드 설정
                .mapY(getStringValue(row, columnIndexMap, HEADER_MAP_Y)) // ✅ [추가] mapY 필드 설정
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

        List<String> missingHeaders = Stream.of(requiredHeaders)
                .map(String::toLowerCase)
                .filter(header -> !columnIndexMap.containsKey(header))
                .toList();

        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("엑셀 파일에 필수 헤더가 존재하지 않습니다: " + missingHeaders);
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