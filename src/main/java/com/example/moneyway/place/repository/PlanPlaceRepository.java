// C:/Users/jjm02/IdeaProjects/MoneyWay1/src/main/java/com/example/moneyway/place/repository/PlanPlaceRepository.java
package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.PlanPlace;
import com.example.moneyway.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {

    List<PlanPlace> findByPlan(Plan plan);

    // [추천] n일차, 방문 순서로 정렬된 결과를 반환하는 메서드 추가
    List<PlanPlace> findByPlanOrderByDayIndexAscOrderIndexAsc(Plan plan);
}