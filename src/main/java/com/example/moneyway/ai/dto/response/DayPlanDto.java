package com.example.moneyway.ai.dto.response;

import java.util.List;


public record DayPlanDto(String day, List<PlaceDto> places, int totalBudget, int usedCost) {}