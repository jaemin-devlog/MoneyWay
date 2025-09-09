package com.example.moneyway.ai.controller;

import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.PlanResponseDto;
import com.example.moneyway.ai.dto.response.PlanSaveResponseDto;
import com.example.moneyway.ai.service.AiPlanService;
import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/ai")
@RequiredArgsConstructor
public class AiPlanController {

    private final AiPlanService aiPlanService;

    // GPT 기반 여행 플랜 생성
    @PostMapping("/plan")
    public ResponseEntity<PlanResponseDto> getAiPlan(@RequestBody TravelPlanRequestDto request) throws Exception {
        PlanResponseDto planResponse = aiPlanService.generatePlanWithAI(request);
        return ResponseEntity.ok(planResponse);
    }

    // 최종 저장
    @PostMapping("/plans")
    public CompletableFuture<ResponseEntity<PlanSaveResponseDto>> createPlanByAi(
            @RequestBody AiPlanCreateRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return aiPlanService.createPlanByAi(request, userDetails.getUser())
                .thenApply(ResponseEntity::ok);
    }

}
