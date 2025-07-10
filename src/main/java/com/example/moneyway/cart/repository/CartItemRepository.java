package com.example.moneyway.cart.repository;

import com.example.moneyway.cart.domain.CartItem;
import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 계획(Plan)에 속한 모든 장바구니 항목을 조회
    List<CartItem> findByPlanOrderByCreatedAtAsc(Plan plan);

    // 특정 계획 내에서 장소가 중복되는지 확인
    boolean existsByPlanAndPlaceIdAndPlaceType(Plan plan, String placeId, PlaceType placeType);

    // ID와 사용자 정보로 장바구니 항목 조회 (수정/삭제 시 본인 확인용)
    Optional<CartItem> findByIdAndUser(Long id, User user);

    // 특정 계획에 속한 모든 장바구니 항목 삭제 (일정표로 모두 옮긴 후 사용)
    void deleteByPlan(Plan plan);

    // 특정 계획에 속한 장바구니 항목들의 예상 비용 총합 계산
    @Query("SELECT SUM(ci.estimatedCost) FROM CartItem ci WHERE ci.plan = :plan")
    Optional<Integer> sumTotalCostByPlan(@Param("plan") Plan plan);
}