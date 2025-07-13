package com.example.moneyway.place.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.common.exception.CustomException.CustomPlaceException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.dto.response.PlaceDetailResponseDto; // DTO 임포트 변경
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;
import com.example.moneyway.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ✅ [최종본] 모든 장소 '조회' 관련 로직을 통합 관리하는 서비스
 * - 단일 PlaceRepository에 의존하여 코드가 매우 간결하고 역할이 명확합니다.
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
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징 처리된 통합 검색 결과 (경량 DTO)
     */
    public Page<PlaceInfoResponseDto> searchPlacesByKeyword(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return Page.empty(pageable);
        }
        return placeRepository.searchByKeyword(keyword, pageable)
                .map(PlaceInfoResponseDto::from);
    }

    /**
     * ✅ [개선] ID로 장소 상세 정보를 찾아 DTO로 반환합니다.
     * 서비스 계층은 외부(컨트롤러)에 엔티티가 아닌 DTO를 반환하는 것을 원칙으로 합니다.
     * @param placeId 장소 ID
     * @return 장소 상세 정보 DTO
     * @throws CustomPlaceException 해당 ID의 장소를 찾지 못했을 경우
     */
    public PlaceDetailResponseDto findPlaceDetailById(Long placeId) {
        Place place = findPlaceById(placeId); // 내부 헬퍼 메서드 사용
        return PlaceDetailResponseDto.from(place);
    }

    /**
     * AI 추천을 위한 후보 장소 목록을 조회합니다.
     * (참고: 이 메서드는 다른 내부 서비스에서 엔티티가 직접 필요할 수 있으므로 엔티티 목록을 반환하는 것이 허용될 수 있습니다.)
     * @param request 사용자의 여행 계획 요청 DTO
     * @return 테마에 맞는 후보 장소 엔티티 목록
     */
    public List<Place> findCandidatesByRequest(TravelPlanRequestDto request) {
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