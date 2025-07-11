package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TourPlaceRepositoryCustomImpl implements TourPlaceRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<TourPlace> findCandidatesByThemes(String areacode, List<String> themes, String excludedTypeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TourPlace> cq = cb.createQuery(TourPlace.class);
        Root<TourPlace> place = cq.from(TourPlace.class);

        // 1. 기본 조건: 지역코드 일치, 특정 콘텐츠 타입 제외
        Predicate defaultConditions = cb.and(
                cb.equal(place.get("areacode"), areacode),
                cb.notEqual(place.get("contenttypeid"), excludedTypeId)
        );

        // 2. 동적 조건: 테마 리스트에 대한 OR 조건 생성
        // (cat1 LIKE '%theme1%' OR title LIKE '%theme1%') OR (cat1 LIKE '%theme2%' OR title LIKE '%theme2%') ...
        List<Predicate> themeOrPredicates = new ArrayList<>();
        for (String theme : themes) {
            Predicate cat1Like = cb.like(place.get("cat1"), "%" + theme + "%");
            Predicate titleLike = cb.like(place.get("title"), "%" + theme + "%");
            themeOrPredicates.add(cb.or(cat1Like, titleLike));
        }
        Predicate themeConditions = cb.or(themeOrPredicates.toArray(new Predicate[0]));

        // 3. 모든 조건을 합쳐서 최종 쿼리 생성
        cq.where(cb.and(defaultConditions, themeConditions));

        return em.createQuery(cq).getResultList();
    }
}