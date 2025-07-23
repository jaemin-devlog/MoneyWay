package com.example.moneyway.plan.domain;

import com.example.moneyway.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plan_place")
public class PlanPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk_id")
    private Place place;

    @Column(nullable = false)
    private Integer dayNumber;

    /**
     * 장바구니에서 가져온, 사용자가 수정했을 수 있는 최종 비용
     */
    private Integer cost;

    private String type;
    private String time;
    private Integer budget;
    private Integer totalPrice;

    @Builder
    public PlanPlace(Plan plan, Place place, Integer dayNumber, Integer cost,
                     String type, String time, Integer budget, Integer totalPrice) {
        this.plan = plan;
        this.place = place;
        this.dayNumber = dayNumber;
        this.cost = cost;  // ✅ 추가
        this.type = type;
        this.time = time;
        this.budget = budget;
        this.totalPrice = totalPrice;
    }

    void setPlan(Plan plan) {
        this.plan = plan;
    }
}
