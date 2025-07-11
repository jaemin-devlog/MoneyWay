package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import java.util.List;

public interface TourPlaceRepositoryCustom {
    List<TourPlace> findCandidatesByThemes(String areacode, List<String> themes, String excludedTypeId);
}