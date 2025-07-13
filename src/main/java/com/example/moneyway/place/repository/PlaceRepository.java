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

import java.util.List;
import java.util.Set;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

    /**
     * 특정 카테고리에 해당하는 장소 목록을 페이징하여 조회합니다.
     * Place 엔티티에 추가한 'category' 인덱스 덕분에 매우 빠르게 동작합니다.
     *
     * @param category 조회할 카테고리 (Enum)
     * @param pageable 페이징 정보
     * @return 페이징된 장소 목록
     */
    Page<Place> findByCategory(PlaceCategory category, Pageable pageable);

    /**
     * 키워드로 모든 종류의 장소를 한 번에 검색합니다.
     * - 장소의 제목(title)을 검색합니다.
     * - 만약 장소가 RestaurantJeju 타입이라면, 메뉴(menu) 정보까지 함께 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 통합 검색 결과
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

    /**
     * 데이터 동기화 시 필요한 중복 확인 쿼리입니다.
     * TourPlace의 contentId로 중복을 확인합니다.
     */
    @Query("SELECT t.contentid FROM TourPlace t WHERE t.contentid IN :contentIds")
    Set<String> findExistingContentIds(@Param("contentIds") List<String> contentIds);

    /**
     * 데이터 동기화 시 필요한 중복 확인 쿼리입니다.
     * RestaurantJeju의 복합 키(title+address)로 중복을 확인합니다.
     */
    @Query("SELECT CONCAT(r.title, '||', r.address) FROM RestaurantJeju r WHERE CONCAT(r.title, '||', r.address) IN :uniqueKeys")
    Set<String> findExistingRestaurantUniqueKeys(@Param("uniqueKeys") List<String> uniqueKeys);

    /**
     * ✅ [추가] 데이터 동기화 작업을 위해 모든 TourPlace 엔티티를 조회합니다.
     * findAll()과 달리 TourPlace 타입만 효율적으로 가져옵니다.
     */
    @Query("SELECT t FROM TourPlace t")
    List<TourPlace> findAllTourPlaces();
}