package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.domain.RestaurantJeju;
import com.example.moneyway.place.domain.TourPlace;

import com.example.moneyway.place.dto.internal.NearbyPlaceDto;
import lombok.Data;
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
     * LEFT JOIN을 통해 TourPlace와 RestaurantJeju 데이터를 함께 가져와 N+1 문제를 해결합니다.
     */
    @Query("SELECT p FROM Place p " +
            "LEFT JOIN TourPlace tp ON p.id = tp.id " +
            "LEFT JOIN RestaurantJeju rj ON p.id = rj.id " +
            "WHERE p.category = :category")
    Page<Place> findByCategory(@Param("category") PlaceCategory category, Pageable pageable);

    /**
     * 모든 장소 목록을 페이징하여 조회합니다.
     * LEFT JOIN을 통해 TourPlace와 RestaurantJeju 데이터를 함께 가져와 N+1 문제를 해결합니다.
     */
    @Query("SELECT p FROM Place p " +
            "LEFT JOIN TourPlace tp ON p.id = tp.id " +
            "LEFT JOIN RestaurantJeju rj ON p.id = rj.id")
    Page<Place> findAll(Pageable pageable);

    /**
     * 키워드로 모든 종류의 장소를 한 번에 검색합니다.
     * LEFT JOIN을 통해 TourPlace와 RestaurantJeju 데이터를 함께 가져와 N+1 문제를 해결합니다.
     */
    @Query(value = """
        SELECT p FROM Place p
        LEFT JOIN TourPlace tp ON p.id = tp.id
        LEFT JOIN RestaurantJeju rj ON p.id = rj.id
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR (TYPE(p) = RestaurantJeju AND LOWER(rj.menu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
            countQuery = """
        SELECT COUNT(p) FROM Place p
        LEFT JOIN TourPlace tp ON p.id = tp.id
        LEFT JOIN RestaurantJeju rj ON p.id = rj.id
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR (TYPE(p) = RestaurantJeju AND LOWER(rj.menu) LIKE LOWER(CONCAT('%', :keyword, '%')))
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

    List<Place> findAllByTitleIn(List<String> titles);


    List<Place> findByTitle(String title);


    /**
     * ai place 추가 부분
     */

    // 관광지/액티비티 (예산 범위 내에서 랜덤 20개)
    @Query(value = """
    SELECT p.place_pk_id AS id,
           p.title AS title,
           p.category AS categoryName,
           tp.addr1 AS address,
           tp.firstimage AS thumbnailUrl,
           tp.price_info AS priceInfo,
           tp.mapx AS mapx,
           tp.mapy AS mapy,
           (6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(tp.mapy)) * COS(RADIANS(tp.mapx) - RADIANS(:lng))
                + SIN(RADIANS(:lat)) * SIN(RADIANS(tp.mapy))
           )) AS distance
    FROM place p
    JOIN tour_place tp ON p.place_pk_id = tp.place_pk_id
    WHERE p.category IN ('TOURIST_ATTRACTION', 'ACTIVITY')
      AND (tp.price_info IS NULL OR CAST(tp.price_info AS UNSIGNED) <= :maxPrice)
    HAVING distance <= :radius
    ORDER BY RAND()
    LIMIT 20
    """, nativeQuery = true)
    List<NearbyPlaceDto> findTourAndActivityNearby(
            @Param("maxPrice") int maxPrice,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );


    // 식당 (예산 + 반경)
    @Query(value = """
    SELECT rj.place_pk_id AS id,
           p.title AS title,
           'RESTAURANT' AS categoryName,
           rj.address AS address,
           rj.img AS thumbnailUrl,
           rj.price_info AS priceInfo,
           rj.mapx AS mapx,
           rj.mapy AS mapy,
           (6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(rj.mapy)) * COS(RADIANS(rj.mapx) - RADIANS(:lng))
                + SIN(RADIANS(:lat)) * SIN(RADIANS(rj.mapy))
           )) AS distance
    FROM restaurant_jeju rj
    JOIN place p ON p.place_pk_id = rj.place_pk_id
    WHERE (rj.price_info IS NULL OR CAST(rj.price_info AS UNSIGNED) <= :maxPrice)
    HAVING distance <= :radius
    ORDER BY RAND()
    LIMIT 20
    """, nativeQuery = true)
    List<NearbyPlaceDto> findRestaurantsNearby(
            @Param("maxPrice") int maxPrice,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );


    // 숙소 (예산 + 반경)
    @Query(value = """
    SELECT p.place_pk_id AS id,
           p.title AS title,
           p.category AS categoryName,
           tp.addr1 AS address,
           tp.firstimage AS thumbnailUrl,
           tp.price_info AS priceInfo,
           tp.mapx AS mapx,
           tp.mapy AS mapy,
           (6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(tp.mapy)) * COS(RADIANS(tp.mapx) - RADIANS(:lng))
                + SIN(RADIANS(:lat)) * SIN(RADIANS(tp.mapy))
           )) AS distance
    FROM place p
    JOIN tour_place tp ON p.place_pk_id = tp.place_pk_id
    WHERE p.category = 'ACCOMMODATION'
      AND (tp.price_info IS NULL OR CAST(tp.price_info AS UNSIGNED) <= :maxPrice)
    HAVING distance <= :radius
    ORDER BY RAND()
    LIMIT 20
    """, nativeQuery = true)
    List<NearbyPlaceDto> findAccommodationsNearby(
            @Param("maxPrice") int maxPrice,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );

    //모든 Place 중 랜덤 하나
    @Query(value = """
    SELECT p.place_pk_id AS id,
           p.title AS title,
           p.category AS categoryName,
           tp.addr1 AS address,
           tp.firstimage AS thumbnailUrl,
           tp.price_info AS priceInfo,
           tp.mapy AS mapy,
           tp.mapx AS mapx
    FROM place p
    JOIN tour_place tp ON p.place_pk_id = tp.place_pk_id
    WHERE p.category = 'ACCOMMODATION'
    ORDER BY RAND()
    LIMIT 1
    """, nativeQuery = true)
        NearbyPlaceDto findRandomAccommodation();


    /**
     * RestaurantJeju 엔티티를 title + address 조합으로 조회합니다.
     */
    @Query("SELECT r FROM RestaurantJeju r WHERE r.title = :title AND r.address = :address")
    Optional<RestaurantJeju> findByTitleAndAddress(@Param("title") String title, @Param("address") String address);

}
