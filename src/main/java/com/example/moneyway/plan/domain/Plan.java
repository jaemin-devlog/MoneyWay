package com.example.moneyway.plan.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 여행 계획(Plan) 엔티티
 * - 한 번에 한 여행 일정
 * - PlanPlace를 통해 여러 장소와 연결
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 여행 스타일 (예: 힐링, 관광, 먹방 등) */
    @Column(name = "travel_style")
    private String travelStyle;

    /** 여행 스타일 수정 */
    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }

    /** 공개 여부 (true: 공개, false: 비공개) */
    private Boolean isPublic = true;

    /** 좋아요 수 (추후 연동) */
    private Integer likeCount = 0;

    // TODO: User 연동 시 주석 해제
    // @ManyToOne(fetch = FetchType.LAZY)
    // private User user;

    /** 여행 제목 */
    private String title;

    /** 전체 예산 */
    private Integer totalBudget;

    /** 인원 수 */
    private Integer personCount;

    /** 1인당 예산 */
    private Integer budgetPerPerson;

    /** 여행 시작일/종료일 */
    private LocalDate startDate;
    private LocalDate endDate;

    /** 생성/수정 시간 */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 여행에 포함된 장소 리스트(PlanPlace와 연결)
     * - PlanPlace: 각 일정별 장소/순서 등 세부정보 관리
     */
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPlace> places = new ArrayList<>();

    /** 최초 생성 시 시간 기록 */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 업데이트 시 시간 갱신 */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** 여행 계획 정보 수정 */
    public void update(String title, Integer totalBudget, Integer personCount,
                       Integer budgetPerPerson, LocalDate startDate, LocalDate endDate,
                       Boolean isPublic) {
        this.title = title;
        this.totalBudget = totalBudget;
        this.personCount = personCount;
        this.budgetPerPerson = budgetPerPerson;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = isPublic;
    }
}
