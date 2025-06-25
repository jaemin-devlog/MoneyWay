package com.example.moneyway.plan.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PlanCreateRequest {
    private Long user_id;
    private String title;
    private Integer total_budget;
    private Integer person_count;
    private Integer budget_per_person;
    private LocalDate start_date;
    private LocalDate end_date;
    private Boolean is_public;
    private String travel_style;
}