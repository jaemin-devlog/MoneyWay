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
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // (1) user_id 필드 추가
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "total_budget")
    private Integer totalBudget;

    @Column(name = "person_count")
    private Integer personCount;

    @Column(name = "budget_per_person")
    private Integer budgetPerPerson;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "travel_style")
    private String travelStyle;

    // 장소 리스트 (생략 가능)
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPlace> places = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanTag> planTags = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** 여행 계획 정보 수정 */
    public void update(String title, Integer totalBudget, Integer personCount,
                       Integer budgetPerPerson, LocalDate startDate, LocalDate endDate,
                       Boolean isPublic, String travelStyle) {
        this.title = title;
        this.totalBudget = totalBudget;
        this.personCount = personCount;
        this.budgetPerPerson = budgetPerPerson;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = isPublic;
        this.travelStyle = travelStyle;
    }
}

