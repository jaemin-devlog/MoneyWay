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

    @Column
    private Integer cost;

    @Column(name = "type") // ✅ DB 컬럼명 같으면 생략 가능
    private String type;

    @Column(name = "time_slot") // ✅ DB 컬럼명 맞춤
    private String time;

    @Column
    private Integer budget;

    @Column(name = "totalPrice") // ✅ DB 컬럼명 정확히 매핑
    private Integer totalPrice;

    @Builder
    public PlanPlace(Plan plan, Place place, Integer dayNumber, Integer cost,
                     String type, String time, Integer budget, Integer totalPrice) {
        this.plan = plan;
        this.place = place;
        this.dayNumber = dayNumber;
        this.cost = cost;
        this.type = type;
        this.time = time;
        this.budget = budget;
        this.totalPrice = totalPrice;
    }

    void setPlan(Plan plan) {
        this.plan = plan;
    }
}

