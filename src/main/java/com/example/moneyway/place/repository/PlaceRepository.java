package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.domain.TourPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

    // ==================== 기본 조회 및 검색 ====================

    /**
     * 특정 카테고리에 해당하는 장소 목록을 페이징하여 조회합니다.
     */
    Page<Place> findByCategory(PlaceCategory category, Pageable pageable);

    /**
     * 키워드로 모든 종류의 장소를 한 번에 검색합니다.
     */
    @Query(value = """
        SELECT p FROM Place p
        LEFT JOIN RestaurantJeju r ON p.id = r.id
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR (TYPE(p) = RestaurantJeju AND LOWER(r.menu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
            countQuery = """
        SELECT COUNT(p) FROM Place p
        LEFT JOIN RestaurantJeju r ON p.id = r.id
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR (TYPE(p) = RestaurantJeju AND LOWER(r.menu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Place> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // ==================== 데이터 동기화(Synchronization)용 ====================

    /**
     * TourPlace의 contentId로 중복을 확인합니다.
     */
    @Query("SELECT t.contentid FROM TourPlace t WHERE t.contentid IN :contentIds")
    Set<String> findExistingContentIds(@Param("contentIds") List<String> contentIds);

    /**
     * RestaurantJeju의 복합 키(title+address)로 중복을 확인합니다.
     */
    @Query("SELECT CONCAT(r.title, '||', r.address) FROM RestaurantJeju r WHERE CONCAT(r.title, '||', r.address) IN :uniqueKeys")
    Set<String> findExistingRestaurantUniqueKeys(@Param("uniqueKeys") List<String> uniqueKeys);

    /**
     * 데이터 동기화 작업을 위해 모든 TourPlace 엔티티를 조회합니다.
     */
    @Query("SELECT t FROM TourPlace t")
    List<TourPlace> findAllTourPlaces();

    /**
     *  주어진 contentId 목록에 해당하는 모든 TourPlace 엔티티를 조회합니다.
     */
    @Query("SELECT t FROM TourPlace t WHERE t.contentid IN :contentIds")
    List<TourPlace> findTourPlacesByContentIds(@Param("contentIds") Collection<String> contentIds);

    @Query("SELECT t FROM TourPlace t WHERE t.contentid = :contentId")
    Optional<TourPlace> findTourPlaceByContentid(@Param("contentId") String contentId);

}