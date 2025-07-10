package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 모든 종류의 장소(Place)를 다형적으로 다루는 리포지토리
 * - 통합 검색 기능 등에서 활용됩니다.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    //장소 이름(title)에 특정 키워드가 포함된 모든 장소를 대소문자 구분 없이 검색합니다.
    List<Place> findByTitleContainingIgnoreCase(String title);
}