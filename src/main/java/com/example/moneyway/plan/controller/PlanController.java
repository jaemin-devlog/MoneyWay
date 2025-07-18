package com.example.moneyway.plan.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.common.exception.ErrorResponse;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.dto.request.PlanCreateRequestDto;
import com.example.moneyway.plan.dto.response.PlanDetailResponseDto;
import com.example.moneyway.plan.dto.response.PlanSummaryResponseDto;
import com.example.moneyway.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "여행 계획(Plan)")
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "여행 계획 생성", description = "사용자가 구성한 시간표를 바탕으로 새로운 여행 계획을 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 계획 생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력 값 유효성 검증 실패 또는 유효하지 않은 장바구니 항목 포함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 만료 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> createPlan(
            @Valid @RequestBody PlanCreateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Plan createdPlan = planService.createPlan(requestDto, userDetails.getUser());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPlan.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "특정 여행 계획 상세 조회", description = "ID로 특정 여행 계획의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 여행 계획",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDetailResponseDto> getPlanDetail(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 변수 할당 없이 바로 반환하여 코드 간소화
        return ResponseEntity.ok(planService.getPlanDetail(planId, userDetails.getUser()));
    }

    @Operation(summary = "나의 모든 여행 계획 목록 조회", description = "현재 로그인한 사용자의 모든 여행 계획 목록을 간략하게 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<PlanSummaryResponseDto>> getAllPlans(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 변수 할당 없이 바로 반환하여 코드 간소화
        return ResponseEntity.ok(planService.getAllPlans(userDetails.getUser()));
    }

    

    @Operation(summary = "여행 계획 삭제", description = "ID로 특정 여행 계획을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 여행 계획",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        planService.deletePlan(planId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}

