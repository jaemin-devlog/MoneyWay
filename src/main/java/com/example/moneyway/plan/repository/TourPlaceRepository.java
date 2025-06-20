package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.TourPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, String> {
    boolean existsByContentid(String contentid);
}