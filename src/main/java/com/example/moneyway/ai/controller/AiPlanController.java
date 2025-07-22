package com.example.moneyway.ai.controller;


import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ai")
@RequiredArgsConstructor
public class FreeAiPlannerController {
    private final FreeAiPlannerService freeAiPlannerService;

    @PostMapping("/plan")
    public ResponseEntity<List<DayPlanDto>> getPlan(@RequestBody TravelPlanRequestDto travelPlanRequestDto) {
        List<DayPlanDto> plan = AiPlannerController
    }
}
