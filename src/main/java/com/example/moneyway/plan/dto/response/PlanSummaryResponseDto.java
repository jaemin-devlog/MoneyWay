package com.example.moneyway.plan.dto.response;

import com.example.moneyway.plan.domain.Plan;
import lombok.Getter;

@Getter
public class PlanSummaryResponseDto {
    private final Long id;
    private final String title;

    private PlanSummaryResponseDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static PlanSummaryResponseDto from(Plan plan) {
        return new PlanSummaryResponseDto(plan.getId(), plan.getTitle());
    }
}