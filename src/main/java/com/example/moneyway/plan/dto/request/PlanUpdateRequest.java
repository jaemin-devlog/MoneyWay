package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PlanUpdateRequest {
    private String title;
    private Integer totalBudget;
    private Integer personCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
}
