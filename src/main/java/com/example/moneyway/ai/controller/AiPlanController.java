package com.example.moneyway.ai.controller;


import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
import com.example.moneyway.ai.dto.response.DayPlanDto;
import com.example.moneyway.ai.service.AiPlanService;
import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/ai")
@RequiredArgsConstructor
public class AiPlanController {


    private final AiPlanService AiPlanService;
    //예산 미리보기
    @PostMapping("/plan")
    public ResponseEntity<List<DayPlanDto>> getFreePlan(@RequestBody TravelPlanRequestDto request) {
        List<DayPlanDto> plan = AiPlanService.generatePlan(request);
        return ResponseEntity.ok(plan);
    }

    /**
     * 무료 AI 기반 여행 계획 생성 및 저장
     */
    //최종 저장 선택시 db에 저장
    @PostMapping("/plans")
    public ResponseEntity<Map<String, Long>> createPlanByFreeAi(
            @RequestBody AiPlanCreateRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long planId = AiPlanService.createPlanByAi(request, userDetails.getUser());
        return ResponseEntity.ok(Map.of("planId", planId));
    }
}
