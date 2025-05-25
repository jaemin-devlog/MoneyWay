package com.example.moneyway.plan.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * PlanPlace
 * - 여행 일정 내 개별 장소(맛집, 명소 등)의 정보를 순서, 날짜, 시간 등과 함께 관리
 * - Plan(여행 일정) : PlanPlace(N) 관계
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

    /** 여행 계획(Plan)과 N:1 연관관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    /** 장소 이름 (Place 엔티티 연동 전 임시, 혹은 실명) */
    private String placeName;

    /** 일정 설명(메모) */
    private String description;

    /** 여행 며칠째(day 1, 2, 3 등) */
    private int day;

    /** 방문 시간대(문자열, 예: 09:00~10:30) */
    private String time;

    // TODO: place_id(Place 테이블과 FK), 순서(order), 위도/경도 등 추가 가능

    /** Plan, Place 엔티티와 연동 시 확장 예시
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "place_id")
     private Place place;
     */
}
