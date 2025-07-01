package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, String> {

    // ✅ contentid 중복 확인용
    boolean existsByContentid(String contentid);

    // ✅ 제주 지역 장소 조회용
    List<TourPlace> findByAreacode(String areacode);

    Optional<TourPlace> findByContentid(String contentid);

//    List<TourPlace> findByCategory(String category);


}
