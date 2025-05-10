package com.example.moneyway.plan.dto;

import com.example.moneyway.plan.domain.Plan;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlanDetailResponse {

    private Long id;
    private String title;
    private Integer totalBudget;
    private Integer personCount;
    private Integer budgetPerPerson;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
    private Integer likeCount;

    // 정적 팩토리 메서드
    public static PlanDetailResponse from(Plan plan) {
        PlanDetailResponse response = new PlanDetailResponse();
        response.id = plan.getId();
        response.title = plan.getTitle();
        response.totalBudget = plan.getTotalBudget();
        response.personCount = plan.getPersonCount();
        response.budgetPerPerson = plan.getBudgetPerPerson();
        response.startDate = plan.getStartDate();
        response.endDate = plan.getEndDate();
        response.isPublic = plan.getIsPublic();
        response.likeCount = plan.getLikeCount();
        return response;
    }
}
