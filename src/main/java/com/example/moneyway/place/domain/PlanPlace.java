// 📦 PlanPlace.java
package com.example.moneyway.place.domain;

import com.example.moneyway.plan.domain.Plan;
import jakarta.persistence.*;
import lombok.*;

/**
 * ✅ 여행 계획에 따라 확정된 장소 배치 정보
 * - 각 Plan의 특정 날짜와 시간대에 어떤 Place를 방문할지를 기록
 * - 장소는 다형성 기반의 Place로 참조됨 (TourPlace, RestaurantJeju 등 모두 가능)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlanPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan; // 소속된 여행 계획

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk_id", nullable = false)
    private Place place; // 다형성 참조

    @Column(name = "day_index")
    private int dayIndex; // n일차

    @Column(name = "time_slot")
    private String timeSlot; // 예: "오전", "14:00"

    @Column(name = "order_index")
    private int orderIndex; // 당일 내 순서

    @Column(name = "estimated_cost")
    private int estimatedCost; // 예상 지출 비용

    @Column(name = "estimated_time")
    private int estimatedTime; // 예상 소요 시간 (분 단위)

    private String memo; // 사용자 메모

    public void update(int dayIndex, String timeSlot, int orderIndex,
                       int estimatedCost, int estimatedTime, String memo) {
        this.dayIndex = dayIndex;
        this.timeSlot = timeSlot;
        this.orderIndex = orderIndex;
        this.estimatedCost = estimatedCost;
        this.estimatedTime = estimatedTime;
        this.memo = memo;
    }
}
