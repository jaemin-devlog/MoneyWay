package com.example.moneyway.plan.controller;

import com.example.moneyway.plan.dto.request.PlanCreateRequest;
import com.example.moneyway.plan.dto.request.PlanPlaceRequest;
import com.example.moneyway.plan.dto.request.PlanStyleRequest;
import com.example.moneyway.plan.dto.request.PlanUpdateRequest;
import com.example.moneyway.plan.dto.response.PlanDetailResponse;
import com.example.moneyway.plan.service.PlanService;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient; // TourApiClient 경로 정규화
import com.example.moneyway.plan.service.TourApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Plan", description = "여행 계획 관련 API")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final TourApiClient tourApiClient;
    private final TourApiService tourApiService;

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

    @Operation(summary = "여행 계획 전체 조회")
    @GetMapping
    public ResponseEntity<List<PlanDetailResponse>> getAllPlans() {
        List<PlanDetailResponse> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @Operation(summary = "여행 계획에 장소 추가")
    @PostMapping("/{id}/places")
    public ResponseEntity<Void> addPlace(
            @PathVariable Long id,
            @RequestBody PlanPlaceRequest request) {
        planService.addPlace(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{planId}/places/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @PathVariable Long planId,
            @PathVariable Long placeId
    ) {
        planService.deletePlace(planId, placeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{planId}/style")
    public ResponseEntity<Void> updateStyle(
            @PathVariable Long planId,
            @RequestBody PlanStyleRequest request
    ) {
        planService.updateTravelStyle(planId, request.getTravelStyle());
        return ResponseEntity.ok().build();
    }

    // TourAPI 활용(정보 가져오기)

}
