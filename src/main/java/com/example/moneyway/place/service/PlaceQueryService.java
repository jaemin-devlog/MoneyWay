package com.example.moneyway.place.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.common.exception.CustomException.CustomPlaceException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.response.PlaceDetailResponseDto;
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;
import com.example.moneyway.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 모든 장소 '조회' 관련 로직을 통합 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceQueryService {

    private final PlaceRepository placeRepository;

    private static final String JEJU_AREA_CODE = "39";

    /**
     * 카테고리별 장소 목록을 조회합니다.
     * @param category 조회할 카테고리 (Enum). null일 경우 전체 장소를 조회합니다.
     * @param pageable 페이징 정보
     * @return 페이징 처리된 장소 목록 (경량 DTO)
     */
    public Page<PlaceInfoResponseDto> findPlacesByCategory(PlaceCategory category, Pageable pageable) {
        Page<Place> placePage;
        if (category == null) {
            placePage = placeRepository.findAll(pageable);
        } else {
            placePage = placeRepository.findByCategory(category, pageable);
        }
        return placePage.map(PlaceInfoResponseDto::from);
    }

    /**
     * 키워드로 모든 장소를 한 번에 검색합니다.
     * ✅ [수정] 키워드가 없을 경우, 전체 장소 목록을 반환하도록 수정되었습니다.
     * @param keyword 검색 키워드 (선택 사항)
     * @param pageable 페이징 정보
     * @return 페이징 처리된 통합 검색 결과 (경량 DTO)
     */
    public Page<PlaceInfoResponseDto> searchPlacesByKeyword(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            // 키워드가 없으면 전체 장소 목록을 조회
            return placeRepository.findAll(pageable)
                    .map(PlaceInfoResponseDto::from);
        } else {
            // 키워드가 있으면 해당 키워드로 검색
            return placeRepository.searchByKeyword(keyword, pageable)
                    .map(PlaceInfoResponseDto::from);
        }
    }

    /**
     * ✅ [개선] ID로 장소 상세 정보를 찾아 DTO로 반환합니다. 파라미터 이름을 placeId로 명확히 수정했습니다.
     * @param placeId 장소의 고유 ID (PK)
     * @return 장소 상세 정보 DTO
     * @throws CustomPlaceException 해당 ID의 장소를 찾지 못했을 경우
     */
    public PlaceDetailResponseDto findPlaceDetailById(Long placeId) {
        Place place = findPlaceById(placeId); // 내부 헬퍼 메서드 사용
        return PlaceDetailResponseDto.from(place);
    }

    /**
     * TourAPI의 contentId로 장소 상세 정보를 찾아 DTO로 반환합니다.
     * @param contentId TourAPI의 콘텐츠 ID
     * @return 장소 상세 정보 DTO
     * @throws CustomPlaceException 해당 ID의 장소를 찾지 못했을 경우
     */
    public PlaceDetailResponseDto findPlaceDetailByContentId(String contentId) {
        // TourPlace는 Place의 하위 타입이므로, Place로 받아도 무방하며 다형성이 유지됩니다.
        Place place = placeRepository.findTourPlaceByContentid(contentId)
                .orElseThrow(() -> new CustomPlaceException(ErrorCode.PLACE_NOT_FOUND));
        return PlaceDetailResponseDto.from(place);
    }

    /**
     * AI 추천을 위한 후보 장소 목록을 조회합니다.
     * ✅ [개선] 요청된 테마가 없을 경우, 불필요한 DB 조회를 막기 위해 즉시 빈 목록을 반환합니다.
     * @param request 사용자의 여행 계획 요청 DTO
     * @return 테마에 맞는 후보 장소 엔티티 목록
     */
    public List<Place> findCandidatesByRequest(TravelPlanRequestDto request) {
        if (request == null || CollectionUtils.isEmpty(request.getThemes())) {
            log.warn("AI 추천 요청에 테마 정보가 비어있어 빈 목록을 반환합니다.");
            return Collections.emptyList();
        }
        return placeRepository.findCandidatesByThemes(JEJU_AREA_CODE, request.getThemes());
    }

    /**
     * ID로 장소 엔티티를 찾아 반환하는 내부 헬퍼 메서드입니다.
     * @param placeId 장소 ID
     * @return Place 엔티티
     */
    private Place findPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomPlaceException(ErrorCode.PLACE_NOT_FOUND));
    }
}