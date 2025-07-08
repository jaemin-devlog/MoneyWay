package com.example.moneyway.ai.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class TravelPlanRequestDto {

    private int budget;
    private int duration;
    private String region;
    private String companion;
    private List<String> themes;
    private String transport;
}