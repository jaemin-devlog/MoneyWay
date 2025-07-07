/*
 * 여행 일정표에서 하루의 순서와 시간대별로 어떤 장소를 방문하는지 정의하는 엔티티.
 * 특정 Plan(여행 계획)에 포함된 Place의 방문 순서, 비용, 소요 시간 등의 정보를 관리한다.
 */
package com.example.moneyway.place.domain;

import com.example.moneyway.plan.domain.Plan;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "plan_id")
    private Plan plan;


    @Column(name = "place_id")
    private Long placeId; // Place와 FK

    @Column(name = "day_index")
    private int dayIndex;

    @Column(name = "time_slot")
    private String timeSlot;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "estimated_cost")
    private int estimatedCost;

    @Column(name = "estimated_time")
    private int estimatedTime;

    public void update(int dayIndex, String timeSlot, int orderIndex, int estimatedCost, int estimatedTime) {
        this.dayIndex = dayIndex;
        this.timeSlot = timeSlot;
        this.orderIndex = orderIndex;
        this.estimatedCost = estimatedCost;
        this.estimatedTime = estimatedTime;
    }

}
