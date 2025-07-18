package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    /**
     * 특정 사용자가 생성한 모든 여행 계획을 조회합니다.
     * @param userId 사용자의 ID
     * @return 해당 사용자의 Plan 목록
     */
    List<Plan> findByUserId(Long userId);
}