package com.example.moneyway.place.service;

import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.repository.TourPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TourPlace 조회 및 필터링 서비스
 * - DB에 저장된 장소들을 조건에 맞게 필터링하여 반환
 */
@Service
@RequiredArgsConstructor
public class TourPlaceService {

    private final TourPlaceRepository tourPlaceRepository;

    /**
     * 사용자의 요청 조건에 맞는 장소 후보 리스트를 반환
     *
     * 현재는 제주도 + 여행 스타일 + 카테고리 기준 간단 필터만 적용
     * 추후 거리/예산 필터 추가 가능
     */
    public List<TourPlace> findCandidatesByRequest(TravelPlanRequestDto request) {
        // 기본적으로 지역이 제주도인 장소만 가져옴
        List<TourPlace> allJejuPlaces = tourPlaceRepository.findByAreacode("39"); // 제주 지역 코드

        // 요청에 포함된 테마가 있을 경우 간단한 필터링
        List<String> themes = request.getThemes();

        return allJejuPlaces.stream()
                .filter(place -> {
                    // 카테고리(cat1)에 테마 키워드가 포함되어 있는지 여부 확인
                    if (themes != null && !themes.isEmpty()) {
                        return themes.stream().anyMatch(theme ->
                                (place.getCat1() != null && place.getCat1().contains(theme)) ||
                                        (place.getTitle() != null && place.getTitle().contains(theme))
                        );
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    public List<TourPlace> findAllJejuPlaces() {
        return tourPlaceRepository.findByAreacode("39");
    }

    public List<TourPlace> findByCat1(String cat1) {
        return tourPlaceRepository.findByAreacodeAndCat1("39", cat1);
    }
}
