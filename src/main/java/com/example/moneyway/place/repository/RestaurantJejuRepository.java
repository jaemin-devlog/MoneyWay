package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.RestaurantJeju;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

// JpaRepository를 상속하면 자동으로 빈으로 등록되므로 @Repository 어노테이션은 생략 가능합니다.
public interface RestaurantJejuRepository extends JpaRepository<RestaurantJeju, Long> {

    Page<RestaurantJeju> findByCategoryCode(String categoryCode, Pageable pageable);

    /**
     * [수정] 키워드로 식당의 제목(title) 또는 메뉴(menu)를 검색합니다. (LIKE 사용)
     * JPQL을 사용하여 객체 지향적으로 쿼리를 작성합니다.
     */
    @Query("""
        SELECT r FROM RestaurantJeju r
        WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(r.menu) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<RestaurantJeju> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * [성능 최적화] 제공된 'title||address' 복합 키 목록과 일치하는
     * DB의 복합 키들을 한 번의 쿼리로 조회합니다.
     * 'findAll'을 사용하는 것보다 월등히 효율적입니다.
     * @param uniqueKeys "title||address" 형태로 조합된 키 목록
     * @return DB에 이미 존재하는 키의 Set
     */
    @Query("SELECT CONCAT(r.title, '||', r.address) FROM RestaurantJeju r WHERE CONCAT(r.title, '||', r.address) IN :uniqueKeys")
    Set<String> findExistingUniqueKeys(@Param("uniqueKeys") List<String> uniqueKeys);
}