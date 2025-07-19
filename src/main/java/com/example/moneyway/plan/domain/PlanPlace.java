package com.example.moneyway.plan.domain;

import com.example.moneyway.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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
    @JoinColumn(name = "place_pk_id") // Place 엔티티의 PK 컬럼명과 일치
    private Place place;

    @Column(nullable = false)
    private Integer dayNumber; // 1일차, 2일차 등을 나타내는 필드

    @Column(nullable = false)
    private LocalTime startTime; // 방문 시작 시간

    @Column(nullable = false)
    private LocalTime endTime; // 방문 종료 시간

    /**
     *  장바구니에서 가져온, 사용자가 수정했을 수 있는 최종 비용을 저장하는 필드
     */
    private Integer cost;

    @Builder
    public PlanPlace(Plan plan, Place place, Integer dayNumber, LocalTime startTime, LocalTime endTime, Integer cost) {
        this.plan = plan;
        this.place = place;
        this.dayNumber = dayNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
    }

    // 양방향 연관관계를 위해 Plan을 설정하는 package-private 메소드
    void setPlan(Plan plan) {
        this.plan = plan;
    }
}