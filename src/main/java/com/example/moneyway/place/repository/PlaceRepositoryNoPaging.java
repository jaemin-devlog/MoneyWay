package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepositoryNoPaging extends JpaRepository<Place, Long> {

    /**
     * 특정 카테고리에 해당하는 장소 목록을 페이징 없이 조회합니다.
     * LEFT JOIN을 통해 TourPlace와 RestaurantJeju 데이터를 함께 가져와 N+1 문제를 해결합니다.
     */
    @Query("SELECT p FROM Place p " +
           "LEFT JOIN TourPlace tp ON p.id = tp.id " +
           "LEFT JOIN RestaurantJeju rj ON p.id = rj.id " +
           "WHERE p.category = :category")
    List<Place> findByCategory(@Param("category") PlaceCategory category);

    /**
     * 모든 장소 목록을 페이징 없이 조회합니다.
     * LEFT JOIN을 통해 TourPlace와 RestaurantJeju 데이터를 함께 가져와 N+1 문제를 해결합니다.
     */
    @Query("SELECT p FROM Place p " +
           "LEFT JOIN TourPlace tp ON p.id = tp.id " +
           "LEFT JOIN RestaurantJeju rj ON p.id = rj.id")
    List<Place> findAll();

    /**
     * 키워드로 모든 종류의 장소를 한 번에 검색합니다. (페이징 없음)
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
    List<Place> searchByKeyword(@Param("keyword") String keyword);
}
