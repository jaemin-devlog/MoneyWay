package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.PlanTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanTagRepository extends JpaRepository<PlanTag, Long> {
    List<PlanTag> findByPlanId(Long planId);
    void deleteByPlanId(Long planId);
}
