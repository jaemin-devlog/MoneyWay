package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import org.springframework.data.domain.Page; // Page 임포트
import org.springframework.data.domain.Pageable; // Pageable 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // [개선] List -> Page로 변경하고 Pageable 파라미터를 추가하여 성능 안정성 확보
    Page<Place> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}