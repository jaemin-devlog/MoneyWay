package com.example.moneyway.plan.service;

import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanTag;
import com.example.moneyway.plan.dto.request.PlanTagRequest;
import com.example.moneyway.plan.dto.response.PlanTagResponse;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.plan.repository.PlanTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanTagService {

    private final PlanTagRepository planTagRepository;
    private final PlanRepository planRepository;

    // 태그 여러 개 추가
    @Transactional
    public void addTags(Long planId, List<PlanTagRequest> requests) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
        for (PlanTagRequest req : requests) {
            planTagRepository.save(
                    PlanTag.builder()
                            .plan(plan)
                            .tagName(req.getTagName())
                            .build()
            );
        }
    }

    // 태그 전체 조회
    @Transactional(readOnly = true)
    public List<PlanTagResponse> getTags(Long planId) {
        return planTagRepository.findByPlanId(planId)
                .stream().map(PlanTagResponse::from).toList();
    }

    // 단일 태그 삭제
    @Transactional
    public void deleteTag(Long tagId) {
        planTagRepository.deleteById(tagId);
    }

    // 계획의 모든 태그 삭제
    @Transactional
    public void deleteAllTags(Long planId) {
        planTagRepository.deleteByPlanId(planId);
    }
}
