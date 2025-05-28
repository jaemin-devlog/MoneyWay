package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PlanCreateRequest {
    private String title;
    private Integer user_id;
    private Integer totalBudget;
    private Integer personCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
}
