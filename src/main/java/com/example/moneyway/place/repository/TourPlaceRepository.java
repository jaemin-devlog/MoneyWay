package com.example.moneyway.place.repository;

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
public interface TourPlaceRepository extends JpaRepository<TourPlace, Long>, TourPlaceRepositoryCustom {

    /**
     * TourAPI에서 '음식점'을 의미하는 콘텐츠 타입 ID입니다.
     * 우연히 제주도의 지역코드(areacode)와 값이 동일하므로 주의가 필요합니다.
     * 이 상수는 contenttypeid를 필터링할 때만 사용해야 합니다.
     */
    String EXCLUDED_CONTENT_TYPE_ID = "39";

    boolean existsByContentid(String contentid);

    Optional<TourPlace> findByContentid(String contentid);

    // ---  JPQL에서도 상수를 파라미터로 바인딩하여 일관성 및 안정성 확보 ---
    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.contenttypeid <> :excludedTypeId")
    List<TourPlace> findByAreacode(@Param("areacode") String areacode, @Param("excludedTypeId") String excludedTypeId);

    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.contenttypeid <> :excludedTypeId")
    Page<TourPlace> findByAreacode(@Param("areacode") String areacode, @Param("excludedTypeId") String excludedTypeId, Pageable pageable);

    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.cat1 = :cat1 AND p.contenttypeid <> :excludedTypeId")
    List<TourPlace> findByAreacodeAndCat1(@Param("areacode") String areacode, @Param("cat1") String cat1, @Param("excludedTypeId") String excludedTypeId);

    @Query("SELECT p FROM TourPlace p WHERE p.areacode = :areacode AND p.cat1 = :cat1 AND p.contenttypeid <> :excludedTypeId")
    Page<TourPlace> findByAreacodeAndCat1(@Param("areacode") String areacode, @Param("cat1") String cat1, @Param("excludedTypeId") String excludedTypeId, Pageable pageable);

    List<TourPlace> findByContentidIn(Collection<String> contentids);

    // ---  TourApiService에서 N+1 문제를 해결하기 위해 필요한 메서드 ---
    @Query("SELECT p.contentid FROM TourPlace p WHERE p.contentid IN :contentIds")
    Set<String> findContentidsIn(@Param("contentIds") Collection<String> contentIds);

    /**
     * 키워드로 관광지와 음식점 모두를 검색합니다. (LIKE 사용)
     * contenttypeid 필터를 제거하여 모든 타입의 장소를 검색 대상으로 포함합니다.
     */

    @Query(value = """
        SELECT p FROM TourPlace p WHERE
        LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """,
            countQuery = """
        SELECT COUNT(p) FROM TourPlace p WHERE
        LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        """)

    Page<TourPlace> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 특정 지역의 장소를 무작위로 N개 조회합니다.
     * 네이티브 쿼리에서도 파라미터 바인딩을 사용하여 안전성과 이식성을 높입니다.
     * 참고: RAND()는 MySQL/H2, RANDOM()은 PostgreSQL/Oracle에서 사용됩니다.
     */
    @Query(value = "SELECT * FROM tour_place WHERE areacode = :areacode AND contenttypeid <> :excludedTypeId ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<TourPlace> findRandomByAreacode(@Param("areacode") String areacode, @Param("excludedTypeId") String excludedTypeId, @Param("size") int size);
}