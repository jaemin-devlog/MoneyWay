package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.PlanPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {
}