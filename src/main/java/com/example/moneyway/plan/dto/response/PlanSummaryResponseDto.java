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
    private String profileImageUrl;
    private int totalPrice;
    private int currentPrice;
    private String period;


    public static PlanSummaryResponseDto from(Plan plan) {
        return PlanSummaryResponseDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .username(plan.getUser().getNickname())
                .profileImageUrl(plan.getUser().getProfileImageUrl())
                .totalPrice(plan.getTotalPrice())
                .currentPrice(plan.getCurrentPrice())
                .period(plan.getPeriod())
                .build();
    }
}