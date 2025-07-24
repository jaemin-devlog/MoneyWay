package com.example.moneyway.ai.dto.response;

import java.util.List;

public record PlanResponseDto(int totalUsedCost, List<DayPlanDto> days) {}
