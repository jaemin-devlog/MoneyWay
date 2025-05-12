package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByIsPublicTrue();
}
