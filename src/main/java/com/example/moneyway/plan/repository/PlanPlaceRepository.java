package com.example.moneyway.plan.repository;

import com.example.moneyway.plan.domain.PlanPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {
    // 대부분의 경우 Plan 엔티티를 통해 접근하므로 복잡한 쿼리는 당장 필요 없을 수 있습니다.
}