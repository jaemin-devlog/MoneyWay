//package com.example.moneyway.ai.controller;
//
//import com.example.moneyway.ai.dto.request.AiPlanCreateRequestDto;
//import com.example.moneyway.ai.dto.request.TravelPlanRequestDto;
//import com.example.moneyway.ai.dto.response.DayPlanDto;
//import com.example.moneyway.ai.service.AiPlannerService;
//import com.example.moneyway.auth.userdetails.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * GPT 기반 여행 일정 추천 요청을 처리하는 API 컨트롤러
// */
//@RestController
//@RequestMapping("/api/ai")
//@RequiredArgsConstructor
//public class AiPlannerController {
//
//    private final AiPlannerService aiPlanService;
//
//    /**
//     * [기존] GPT 기반 여행 일정 추천 '목록'만 요청
//     */
//    @PostMapping("/planner")
//    public ResponseEntity<List<DayPlanDto>> getPlan(@RequestBody TravelPlanRequestDto request) {
//        List<DayPlanDto> recommendedPlan = aiPlannerService.getRecommendedPlan(request);
//        return ResponseEntity.ok(recommendedPlan);
//    }
//
//    /**
//     * [신규] AI 추천을 받아 '여행 계획(Plan)'을 즉시 생성 및 저장
//     */
//    @PostMapping("/plans")
//    public ResponseEntity<Map<String, Long>> createPlanByAi(
//            @RequestBody AiPlanCreateRequestDto request,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//        Long planId = aiPlannerService.createPlanByAi(request, userDetails.getUser());
//
//        // 생성된 Plan의 ID를 반환하여 클라이언트가 해당 계획 상세 페이지로 이동할 수 있도록 함
//        return ResponseEntity.ok(Map.of("planId", planId));
//    }
//}
