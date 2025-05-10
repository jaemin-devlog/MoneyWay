package com.example.moneyway.plan.controller;

import com.example.moneyway.plan.dto.PlanCreateRequest;
import com.example.moneyway.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@RequestBody PlanCreateRequest request) {
        Long planId = planService.createPlan(request);
        return ResponseEntity.ok(Map.of("planId", planId));
    }
}
