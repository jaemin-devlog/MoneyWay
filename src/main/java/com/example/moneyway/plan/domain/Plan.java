package com.example.moneyway.plan.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isPublic = true;
    private Integer likeCount = 0;

    // User 없이 일단 진행
    // @ManyToOne(fetch = FetchType.LAZY)
    // private User user;

    private String title;
    private Integer totalBudget;
    private Integer personCount;
    private Integer budgetPerPerson;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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
