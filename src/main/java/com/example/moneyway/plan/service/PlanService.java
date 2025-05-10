package com.example.moneyway.plan.service;

import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.dto.PlanCreateRequest;
import com.example.moneyway.plan.dto.PlanDetailResponse;
import com.example.moneyway.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    /**
     * 여행 계획 생성
     */
    public Long createPlan(PlanCreateRequest request) {
        if (request.getPersonCount() == null || request.getPersonCount() == 0) {
            throw new IllegalArgumentException("인원 수는 0보다 커야 합니다.");
        }

        int perPerson = request.getTotalBudget() / request.getPersonCount();

        Plan plan = Plan.builder()
                .title(request.getTitle())
                .totalBudget(request.getTotalBudget())
                .personCount(request.getPersonCount())
                .budgetPerPerson(perPerson)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isPublic(request.getIsPublic())
                .likeCount(0)
                .build();

        return planRepository.save(plan).getId();
    }

    /**
     * 여행 계획 상세 조회
     */
    public PlanDetailResponse getPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 Plan이 존재하지 않습니다."));

        return PlanDetailResponse.from(plan);
    }

    /**
     * 여행 계획 삭제
     */
    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new IllegalArgumentException("해당 ID의 Plan이 존재하지 않습니다.");
        }
        planRepository.deleteById(planId);
    }


}
