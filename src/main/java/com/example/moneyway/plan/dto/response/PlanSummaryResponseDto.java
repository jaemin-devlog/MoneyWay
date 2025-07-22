package com.example.moneyway.plan.dto.response;

import com.example.moneyway.plan.domain.Plan;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanSummaryResponseDto {
    private Long id;
    private String title;
    private String username;
    private int totalPrice;

    public static PlanSummaryResponseDto from(Plan plan) {
        return PlanSummaryResponseDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .username(plan.getUser().getNickname())
                .totalPrice(plan.getTotalPrice())
                .build();
    }
}