package com.example.moneyway.place.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.response.PlaceSearchResultDto;
import com.example.moneyway.place.repository.RestaurantJejuRepository;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * ✅ 모든 장소 '조회' 관련 로직을 통합 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceQueryService {

    private final RestaurantJejuRepository restaurantRepository;
    private final TourPlaceRepository tourPlaceRepository;
    // ✅ [수정] 비동기 Executor 제거
    // private final Executor dbTaskExecutor;

    private static final String JEJU_AREA_CODE = "39";

    /**
     * ✅ [수정] 키워드로 관광지와 맛집을 '동기적'으로 검색합니다.
     * 비동기 처리의 복잡성을 제거하여 안정성을 높입니다.
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징 처리된 통합 검색 결과
     */
    public Page<PlaceSearchResultDto> searchPlacesByKeyword(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return Page.empty(pageable);
        }

        // 1. 맛집을 먼저 검색합니다.
        Page<RestaurantJeju> restaurantPage = restaurantRepository.searchByKeyword(keyword, pageable);

        // 2. 관광지를 다음으로 검색합니다.
        Page<TourPlace> tourPlacePage = tourPlaceRepository.searchByKeyword(keyword, pageable);

        // 3. 두 검색 결과를 합칩니다.
        return combineSearchResults(restaurantPage, tourPlacePage, pageable);
    }

    /**
     * 두 개의 다른 타입의 Page 객체를 병합하는 private 도우미 메서드입니다.
     */
    private Page<PlaceSearchResultDto> combineSearchResults(Page<? extends Place> page1, Page<? extends Place> page2, Pageable pageable) {
        List<PlaceSearchResultDto> combinedContent = Stream.concat(
                page1.getContent().stream().map(PlaceSearchResultDto::from),
                page2.getContent().stream().map(PlaceSearchResultDto::from)
        ).toList();

        long totalElements = page1.getTotalElements() + page2.getTotalElements();
        return new PageImpl<>(combinedContent, pageable, totalElements);
    }


    /**
     * 맛집 목록을 조회합니다. (카테고리별/전체)
     * @param categoryCode 맛집 카테고리 코드 (선택)
     * @param pageable 페이징 정보
     * @return 페이징 처리된 맛집 목록
     */
    public Page<PlaceSearchResultDto> findRestaurants(String categoryCode, Pageable pageable) {
        Page<RestaurantJeju> restaurantPage;
        if (!StringUtils.hasText(categoryCode)) {
            restaurantPage = restaurantRepository.findAll(pageable);
        } else {
            restaurantPage = restaurantRepository.findByCategoryCode(categoryCode, pageable);
        }
        return restaurantPage.map(PlaceSearchResultDto::from);
    }

    /**
     * 관광지 목록을 조회합니다. (카테고리별/전체)
     * 이 메서드는 의도적으로 음식점을 제외하여 순수 관광 명소만 가져옵니다.
     * @param category 관광지 카테고리 코드 (선택)
     * @param pageable 페이징 정보
     * @return 페이징 처리된 관광지 목록
     */
    public Page<PlaceSearchResultDto> findTourPlaces(String category, Pageable pageable) {
        Page<TourPlace> tourPlacePage;
        if (!StringUtils.hasText(category)) {
            tourPlacePage = tourPlaceRepository.findByAreacode(JEJU_AREA_CODE, TourPlaceRepository.EXCLUDED_CONTENT_TYPE_ID, pageable);
        } else {
            tourPlacePage = tourPlaceRepository.findByAreacodeAndCat1(JEJU_AREA_CODE, category, TourPlaceRepository.EXCLUDED_CONTENT_TYPE_ID, pageable);
        }
        return tourPlacePage.map(PlaceSearchResultDto::from);
    }

    /**
     * contentId로 특정 관광지 정보를 조회합니다.
     * @param contentid 관광지 고유 ID
     * @return Optional<TourPlace>
     */
    public Optional<TourPlace> findByContentId(String contentid) {
        return tourPlaceRepository.findByContentid(contentid);
    }

    /**
     * 제주도 관광지를 무작위로 N개 조회합니다.
     * 이 메서드 또한 의도적으로 음식점을 제외합니다.
     * @param size 조회할 개수
     * @return 무작위 관광지 목록
     */
    public List<TourPlace> getRandomJejuPlaces(int size) {
        return tourPlaceRepository.findRandomByAreacode(JEJU_AREA_CODE, TourPlaceRepository.EXCLUDED_CONTENT_TYPE_ID, size);
    }

    /**
     * AI 추천을 위한 후보 장소 목록을 조회합니다.
     * 이 메서드 또한 의도적으로 음식점을 제외합니다.
     * @param request 사용자의 여행 계획 요청 DTO
     * @return 테마에 맞는 후보 장소 목록
     */
    public List<TourPlace> findCandidatesByRequest(TravelPlanRequestDto request) {
        List<String> themes = request.getThemes();
        if (themes == null || themes.isEmpty()) {
            return tourPlaceRepository.findByAreacode(JEJU_AREA_CODE, TourPlaceRepository.EXCLUDED_CONTENT_TYPE_ID);
        }
        // 참고: findCandidatesByThemes는 제공된 리포지토리에 정의되어 있지 않으므로, 실제 구현이 필요합니다.
        return tourPlaceRepository.findByAreacode(JEJU_AREA_CODE, TourPlaceRepository.EXCLUDED_CONTENT_TYPE_ID);
    }
}