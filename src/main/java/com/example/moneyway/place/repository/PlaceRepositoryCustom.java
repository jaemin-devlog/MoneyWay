package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import java.util.List;

public interface PlaceRepositoryCustom {

    /**
     * AI 추천을 위한 후보 장소 목록을 동적으로 조회합니다.
     * - 특정 지역(areacode)에 속해야 합니다.
     * - 맛집, 카페 카테고리는 제외합니다.
     * - 주어진 테마 목록과 장소의 제목 또는 카테고리(cat1)가 일치해야 합니다.
     *
     * @param areacode 지역 코드 (예: "39" for 제주)
     * @param themes 검색할 테마 목록
     * @return 조건에 맞는 장소 후보 목록
     */
    List<Place> findCandidatesByThemes(String areacode, List<String> themes);
}