package com.example.moneyway.plan.dto.response;

import com.example.moneyway.plan.domain.Plan;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PlanDetailResponse {

    private Long id;
    private String title;
    private String travelStyle;
    private Integer totalBudget;
    private Integer personCount;
    private Integer budgetPerPerson;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
    private Integer likeCount;
    private List<PlanPlaceResponse> places;

    // 정적 팩토리 메서드
    public static PlanDetailResponse from(Plan plan) {
        PlanDetailResponse response = new PlanDetailResponse();
        response.id = plan.getId();
        response.title = plan.getTitle();
        response.travelStyle = plan.getTravelStyle();
        response.totalBudget = plan.getTotalBudget();
        response.personCount = plan.getPersonCount();
        response.budgetPerPerson = plan.getBudgetPerPerson();
        response.startDate = plan.getStartDate();
        response.endDate = plan.getEndDate();
        response.isPublic = plan.getIsPublic();
        response.likeCount = plan.getLikeCount();

        response.places = plan.getPlaces().stream()
                .map(PlanPlaceResponse::from)
                .collect(Collectors.toList());
        return response;
    }
}
