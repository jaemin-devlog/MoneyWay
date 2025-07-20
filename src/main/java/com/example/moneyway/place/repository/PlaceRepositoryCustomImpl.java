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
    public List<Place> findCandidatesByThemes(List<String> themes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Place> cq = cb.createQuery(Place.class);
        Root<Place> place = cq.from(Place.class);

        Root<TourPlace> tourPlace = cb.treat(place, TourPlace.class);

        List<Predicate> finalPredicates = new ArrayList<>();

        // 제주도 지역 코드 (하드코딩)
        finalPredicates.add(cb.equal(tourPlace.get("areacode"), "39"));
        // 맛집, 카페 카테고리 제외
        finalPredicates.add(place.get("category").in(PlaceCategory.RESTAURANT, PlaceCategory.CAFE).not());

        if (!CollectionUtils.isEmpty(themes)) {
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

        cq.where(finalPredicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}