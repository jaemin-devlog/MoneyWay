//package com.example.moneyway.plan.service;
//
//import com.example.moneyway.plan.domain.Plan;
//import com.example.moneyway.place.domain.PlanPlace;
//import com.example.moneyway.plan.dto.request.*;
//import com.example.moneyway.plan.dto.response.PlanDetailResponse;
//import com.example.moneyway.plan.repository.PlanRepository;
//import com.example.moneyway.place.repository.PlanPlaceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class PlanService {
//
//    private final PlanRepository planRepository;
//    private final PlanPlaceRepository planPlaceRepository;
//
//    /**
//     * 여행 계획 생성
//     */
//    @Transactional
//    public Long createPlan(PlanCreateRequest request) {
//        Plan plan = Plan.builder()
//                .userId(request.getUser_id()) // userId 꼭 세팅!
//                .title(request.getTitle())
//                .totalBudget(request.getTotal_budget())
//                .personCount(request.getPerson_count())
//                .budgetPerPerson(request.getBudget_per_person())
//                .startDate(request.getStart_date())
//                .endDate(request.getEnd_date())
//                .isPublic(request.getIs_public())
//                .travelStyle(request.getTravel_style())
//                .build();
//        planRepository.save(plan);
//        return plan.getId();
//    }
//
//    /**
//     * 여행 계획 상세 조회
//     */
//    @Transactional(readOnly = true)
//    public PlanDetailResponse getPlan(Long id) {
//        Plan plan = planRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
//        return PlanDetailResponse.from(plan);
//    }
//
//    /**
//     * 여행 계획 전체 조회
//     */
//    @Transactional(readOnly = true)
//    public List<PlanDetailResponse> getAllPlans() {
//        List<Plan> plans = planRepository.findAll();
//        return plans.stream().map(PlanDetailResponse::from).toList();
//    }
//
//    /**
//     * 여행 계획 수정
//     */
//    @Transactional
//    public void updatePlan(Long id, PlanUpdateRequest request) {
//        Plan plan = planRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
//        plan.update(
//                request.getTitle(),
//                request.getTotal_budget(),
//                request.getPerson_count(),
//                request.getBudget_per_person(),
//                request.getStart_date(),
//                request.getEnd_date(),
//                request.getIs_public(),
//                request.getTravel_style()
//        );
//        // 변경감지로 자동저장 (JPA)
//    }
//
//    /**
//     * 여행 계획 삭제
//     */
//    @Transactional
//    public void deletePlan(Long id) {
//        planRepository.deleteById(id);
//    }
//
//    /**
//     * 여행 계획에 장소 추가
//     */
//    @Transactional
//    public void addPlace(Long planId, PlanPlaceRequest request) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
//        PlanPlace planPlace = PlanPlace.builder()
//                .plan(plan)
//                .placeId(request.getPlaceId())
//                .dayIndex(request.getDayIndex())
//                .timeSlot(request.getTimeSlot())
//                .orderIndex(request.getOrderIndex())
//                .estimatedCost(request.getEstimatedCost())
//                .estimatedTime(request.getEstimatedTime())
//                .build();
//        planPlaceRepository.save(planPlace);
//        plan.getPlaces().add(planPlace);
//    }
//
//    @Transactional
//    public void updatePlace(Long planId, PlanPlaceRequest request) {
//        PlanPlace planPlace = planPlaceRepository.findById(request.getPlaceId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
//
//        // planId와 일치하는지 확인하여 외부에서 다른 계획의 장소를 수정 못하게 막기
//        if (!planPlace.getPlan().getId().equals(planId)) {
//            throw new IllegalArgumentException("해당 계획에 속한 장소가 아닙니다.");
//        }
//
//        // 엔티티에 업데이트 로직 추가하거나 setter로 직접 수정
//        planPlace.update(
//                request.getDayIndex(),
//                request.getTimeSlot(),
//                request.getOrderIndex(),
//                request.getEstimatedCost(),
//                request.getEstimatedTime()
//        );
//    }
//
//    /**
//     * 여행 계획에서 장소 삭제
//     */
//    @Transactional
//    public void deletePlace(Long planId, Long placeId) {
//        PlanPlace planPlace = planPlaceRepository.findById(placeId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
//        planPlaceRepository.delete(planPlace);
//    }
//
//    /**
//     * 여행 스타일 변경
//     */
//    @Transactional
//    public void updateTravelStyle(Long planId, String travelStyle) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
//        plan.setTravelStyle(travelStyle);
//    }
//
//    /**
//     * PlanPlace 목록에서 사용된 비용 합산
//     * 총 예산 - 사용 예산 = 잔여 예산 계산
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Integer> getBudget(Long planId) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계획입니다."));
//
//        // PlanPlace 전체 조회 → cost 합산
//        List<PlanPlace> places = planPlaceRepository.findByPlan(plan);
//        int used = places.stream()
//                .mapToInt(PlanPlace::getEstimatedCost)
//                .sum();
//
//        int total = plan.getTotalBudget();
//        int remaining = total - used;
//
//        return Map.of(
//                "totalBudget", total,
//                "usedBudget", used,
//                "remainingBudget", remaining
//        );
//    }
//
//}
//
