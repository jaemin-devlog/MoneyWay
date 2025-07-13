package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.domain.TourPlace;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Place> findCandidatesByThemes(String areacode, List<String> themes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Place> cq = cb.createQuery(Place.class);
        Root<Place> place = cq.from(Place.class);

        // cb.treat를 사용하여 Place를 TourPlace 타입으로 다루고, 자식 필드에 접근합니다.
        Root<TourPlace> tourPlace = cb.treat(place, TourPlace.class);

        // ✅ [개선] 최종 WHERE 절에 들어갈 모든 AND 조건들을 담을 리스트
        List<Predicate> finalPredicates = new ArrayList<>();

        // 1. 기본 조건 추가: 지역코드 일치 및 특정 카테고리(맛집, 카페) 제외
        finalPredicates.add(cb.equal(tourPlace.get("areacode"), areacode));
        finalPredicates.add(place.get("category").in(PlaceCategory.RESTAURANT, PlaceCategory.CAFE).not());

        // 2. 동적 테마 조건 추가: 테마 목록이 비어있지 않은 경우에만 생성
        if (!CollectionUtils.isEmpty(themes)) {
            // ✅ [개선] for-loop를 Stream API로 변경하여 코드를 더 간결하고 함수형으로 개선
            Predicate themeConditions = cb.or(
                    themes.stream()
                            .map(theme -> {
                                Predicate titleLike = cb.like(place.get("title"), "%" + theme + "%");
                                Predicate cat1Like = cb.like(tourPlace.get("cat1"), "%" + theme + "%");
                                return cb.or(titleLike, cat1Like);
                            })
                            .toArray(Predicate[]::new)
            );
            finalPredicates.add(themeConditions);
        }

        // 3. 완성된 조건들을 쿼리에 적용
        cq.where(finalPredicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}