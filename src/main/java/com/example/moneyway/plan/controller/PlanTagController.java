package com.example.moneyway.plan.controller;

import com.example.moneyway.plan.dto.request.PlanTagRequest;
import com.example.moneyway.plan.dto.response.PlanTagResponse;
import com.example.moneyway.plan.service.PlanTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Tag(name = "PlanTag", description = "플랜 태그 API")
@RestController
@RequestMapping("/api/plans/{planId}/tags")
@RequiredArgsConstructor
public class PlanTagController {

    private final PlanTagService planTagService;

    // 태그 여러 개 추가
    @PostMapping
    public ResponseEntity<Void> addTags(
            @PathVariable Long planId,
            @RequestBody List<PlanTagRequest> tagRequests) {
        planTagService.addTags(planId, tagRequests);
        return ResponseEntity.ok().build();
    }

    // 태그 전체 조회
    @GetMapping
    public ResponseEntity<List<PlanTagResponse>> getTags(@PathVariable Long planId) {
        return ResponseEntity.ok(planTagService.getTags(planId));
    }

    // 단일 태그 삭제
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        planTagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    // 계획의 전체 태그 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteAllTags(@PathVariable Long planId) {
        planTagService.deleteAllTags(planId);
        return ResponseEntity.noContent().build();
    }
}
