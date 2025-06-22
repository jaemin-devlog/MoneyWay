package com.example.moneyway.plan.domain;

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

    /** 여행 계획(Plan)과 N:1 연관관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    /** Place 엔티티 연동 (만약 없다면 Long 필드로) */
    @Column(name = "place_id")
    private Long placeId; // Place와 FK

    /** 여행 며칠째(day 1, 2, 3 등) */
    @Column(name = "day_index")
    private int dayIndex;

    /** 시간대 (varchar(20)) */
    @Column(name = "time_slot")
    private String timeSlot;

    /** 하루 중 순서 (order_index) */
    @Column(name = "order_index")
    private int orderIndex;

    /** 예상 비용 */
    @Column(name = "estimated_cost")
    private int estimatedCost;

    /** 예상 소요시간(분) */
    @Column(name = "estimated_time")
    private int estimatedTime;

    // 필요에 따라 placeName, description 등 추가 정보 필드 사용 가능!
}
