package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.RestaurantJeju;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;         // Page 임포트
import org.springframework.data.domain.Pageable;     // Pageable 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantJejuRepository extends JpaRepository<RestaurantJeju, Long> {

    // [수정] 카테고리 코드로 맛집 목록을 페이징하여 조회
    Page<RestaurantJeju> findByCategoryCode(String categoryCode, Pageable pageable);

    // title과 address로 존재 여부를 확인하는 메서드 (단일 체크용)
    boolean existsByTitleAndAddress(String title, String address);

    @Query("SELECT r FROM RestaurantJeju r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.menu) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RestaurantJeju> searchByKeyword(@Param("keyword") String keyword);

    // 참고: findAll(Pageable pageable) 메서드는 JpaRepository에 이미 정의되어 있어 별도로 선언할 필요가 없습니다.
}