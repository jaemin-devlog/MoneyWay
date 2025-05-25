package com.example.moneyway.plan.service;

import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanPlace;
import com.example.moneyway.plan.dto.request.*;
import com.example.moneyway.plan.dto.response.PlanDetailResponse;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.plan.repository.PlanPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanPlaceRepository planPlaceRepository;

    /**
     * 여행 계획 생성
     */
    @Transactional
    public Long createPlan(PlanCreateRequest request) {
        Plan plan = Plan.builder()
                .title(request.getTitle())
                .totalBudget(request.getTotalBudget())
                .personCount(request.getPersonCount())
                .budgetPerPerson(request.getTotalBudget() / request.getPersonCount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isPublic(request.getIsPublic())
                .build();
        planRepository.save(plan);
        return plan.getId();
    }

    /**
     * 여행 계획 상세 조회
     */
    @Transactional(readOnly = true)
    public PlanDetailResponse getPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
        return PlanDetailResponse.from(plan);
    }

    /**
     * 여행 계획 전체 조회
     */
    @Transactional(readOnly = true)
    public List<PlanDetailResponse> getAllPlans() {
        List<Plan> plans = planRepository.findAll();
        return plans.stream().map(PlanDetailResponse::from).toList();
    }

    /**
     * 여행 계획 수정
     */
    @Transactional
    public void updatePlan(Long id, PlanUpdateRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
        plan.update(
                request.getTitle(),
                request.getTotalBudget(),
                request.getPersonCount(),
                request.getTotalBudget() / request.getPersonCount(),
                request.getStartDate(),
                request.getEndDate(),
                request.getIsPublic()
        );
        // 엔티티 변경감지로 자동 저장됨 (JPA)
    }

    /**
     * 여행 계획 삭제
     */
    @Transactional
    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }

    /**
     * 여행 계획에 장소 추가
     */
    @Transactional
    public void addPlace(Long planId, PlanPlaceRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
        PlanPlace planPlace = PlanPlace.builder()
                .plan(plan)
                .placeName(request.getPlaceName())
                .description(request.getDescription())
                .day(request.getDay())
                .time(request.getTime())
                .build();
        planPlaceRepository.save(planPlace);
        plan.getPlaces().add(planPlace);
    }

    /**
     * 여행 계획에서 장소 삭제
     */
    @Transactional
    public void deletePlace(Long planId, Long placeId) {
        PlanPlace planPlace = planPlaceRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        planPlaceRepository.delete(planPlace);
    }

    /**
     * 여행 스타일 변경
     */
    @Transactional
    public void updateTravelStyle(Long planId, String travelStyle) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
        plan.setTravelStyle(travelStyle);
    }
}
