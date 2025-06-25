package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.PlanPlace;
import com.example.moneyway.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {
    /**
     * 특정 Plan 에 포함된 PlanPlace 목록 조회
     * 예산 계산 또는 시간표 구성을 위해 사용
     */
    List<PlanPlace> findByPlan(Plan plan);
}
