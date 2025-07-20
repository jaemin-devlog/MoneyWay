package com.example.moneyway.ai.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class TravelPlanRequestDto {

    private int budget;
    private int duration;
    private int companion; // String -> int
    private List<String> themes;
}