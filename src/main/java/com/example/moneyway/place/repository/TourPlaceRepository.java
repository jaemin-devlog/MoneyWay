package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * ✅ TourPlace 엔티티에 대한 데이터 접근을 처리하는 리포지토리
 * - 기본 키(PK)는 Long 타입입니다.
 * - [중요] 모든 조회 쿼리에서 음식점(contenttypeid='39')은 제외합니다.
 */
@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, Long> {

    boolean existsByContentid(String contentid);

    Optional<TourPlace> findByContentid(String contentid);

    // 지역 코드로 조회 시 음식점 제외
    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.contenttypeid <> '39'")
    List<TourPlace> findByAreacode(@Param("areacode") String areacode);

    // 지역 코드와 카테고리로 조회 시 음식점 제외
    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.cat1 = :cat1 AND p.contenttypeid <> '39'")
    List<TourPlace> findByAreacodeAndCat1(@Param("areacode") String areacode, @Param("cat1") String cat1);

    List<TourPlace> findByContentidIn(Collection<String> contentids);

    // 키워드 검색 시 음식점 제외
    @Query("SELECT p FROM TourPlace p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.overview) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.infotext) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.subname) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.contenttypeid <> '39'") // 👈 음식점(39) 제외 조건 추가
    List<TourPlace> searchByKeyword(@Param("keyword") String keyword);
}