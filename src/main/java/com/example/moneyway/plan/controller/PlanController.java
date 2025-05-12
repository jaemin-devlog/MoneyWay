package com.example.moneyway.plan.controller;

import com.example.moneyway.plan.dto.PlanCreateRequest;
import com.example.moneyway.plan.dto.PlanDetailResponse;
import com.example.moneyway.plan.dto.PlanUpdateRequest;
import com.example.moneyway.plan.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.util.Map;

@Tag(name = "Plan", description = "여행 계획 관련 API")
@RestController
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    /**
     * 여행 계획 생성
     */
    @Operation(summary = "여행 계획 생성")
    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@RequestBody PlanCreateRequest request) {
        Long planId = planService.createPlan(request);
        return ResponseEntity.ok(Map.of("planId", planId));
    }

    /**
     * 여행 계획 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailResponse> getPlan(@PathVariable Long id) {
        PlanDetailResponse response = planService.getPlan(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 여행 계획 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 여행 계획 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Long>> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanUpdateRequest request) {

        planService.updatePlan(id, request);
        return ResponseEntity.ok(Map.of("updatedPlanId", id));
    }
}
