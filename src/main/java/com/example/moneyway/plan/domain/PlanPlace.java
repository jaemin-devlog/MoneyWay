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
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk_id", nullable = false)
    private Place place;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(nullable = false)
    private Integer dayNumber; // 1일차, 2일차 등

    @Column
    private Integer cost; // 개별 장소 비용

    @Column(name = "type")
    private String type; // 카테고리명

    @Column(name = "time_slot")
    private String time; // 오전 / 오후 등

    @Column
    private Integer budget; // 예산

    @Column(name = "totalPrice")
    private Integer totalPrice; // 하루 총 비용

    // ✅ 새로 추가된 필드
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // 방문 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // 방문 종료 시간

    @Builder
    public PlanPlace(Plan plan, Place place, String placeName, Integer dayNumber, Integer cost,
                     String type, String time, Integer budget, Integer totalPrice,
                     LocalTime startTime, LocalTime endTime) {
        this.plan = plan;
        this.place = place;
        this.placeName = placeName;   // 장소 이름 문자열 저장
        this.dayNumber = dayNumber;
        this.cost = cost;
        this.type = type;
        this.time = time;
        this.budget = budget;
        this.totalPrice = totalPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    void setPlan(Plan plan) {
        this.plan = plan;
    }
}
