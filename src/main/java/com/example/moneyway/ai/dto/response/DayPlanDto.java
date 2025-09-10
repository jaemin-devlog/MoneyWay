package com.example.moneyway.ai.dto.response;

import com.example.moneyway.place.domain.AiPlaceDto;

import java.util.List;


public record DayPlanDto(
        String day,
        List<AiPlaceDto> places,
        int totalBudget,
        int usedCost
) {}