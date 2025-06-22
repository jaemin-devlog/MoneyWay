package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, String> {
    boolean existsByContentid(String contentid);
}