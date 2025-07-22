package com.example.moneyway.plan.service;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.cart.repository.CartRepository;
import com.example.moneyway.common.exception.CustomException.CustomPlanException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.plan.domain.PlanPlace;
import com.example.moneyway.plan.dto.request.PlanCreateRequestDto;
import com.example.moneyway.plan.dto.request.PlanPlaceCreateDto;
import com.example.moneyway.plan.dto.response.PlanDetailResponseDto;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.plan.dto.response.PlanSummaryResponseDto;
import com.example.moneyway.plan.repository.PlanPlaceRepository;
import com.example.moneyway.plan.repository.PlanRepository;
import com.example.moneyway.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final CartRepository cartRepository;
    private final PlanPlaceRepository planPlaceRepository; // 의존성 주입 추가
    private final PlaceRepository placeRepository;       // 의존성 주입 추가

    @Transactional
    public Plan createPlan(PlanCreateRequestDto requestDto, User user) {
        Plan plan = Plan.builder()
                .title(requestDto.getTitle())
                .totalPrice(requestDto.getTotalPrice())
                .user(user)
                .build();

        List<Cart> usedCarts = new ArrayList<>();
        for (PlanPlaceCreateDto placeDto : requestDto.getPlaces()) {
            Cart cart = cartRepository.findByIdAndUserId(placeDto.getCartId(), user.getId())
                    .orElseThrow(() -> new CustomPlanException(ErrorCode.INVALID_CART_ITEM_FOR_PLAN));
            usedCarts.add(cart);

            PlanPlace planPlace = PlanPlace.builder()
                    .place(cart.getPlace())
                    .cost(cart.getPrice())
                    .dayNumber(placeDto.getDayNumber())
                    .startTime(placeDto.getStartTime())
                    .endTime(placeDto.getEndTime())
                    .build();

            plan.addPlanPlace(planPlace);
        }

        Plan savedPlan = planRepository.save(plan);
        cartRepository.deleteAll(usedCarts);

        return savedPlan;
    }

    

    @Transactional(readOnly = true)
    public PlanDetailResponseDto getPlanDetail(Long planId, User user) {
        Plan plan = findPlanByIdAndValidateOwner(planId, user.getId());
        return PlanDetailResponseDto.from(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanSummaryResponseDto> getAllPlans(User user) {
        return planRepository.findByUserId(user.getId()).stream()
                .map(PlanSummaryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePlan(Long planId, com.example.moneyway.plan.dto.request.PlanUpdateRequestDto requestDto, User user) {
        // 1. 기존 계획을 찾고, 소유자가 맞는지 검증합니다.
        Plan plan = findPlanByIdAndValidateOwner(planId, user.getId());

        // 2. 계획의 제목 등 기본 정보를 업데이트합니다.
        plan.setTitle(requestDto.getTitle());
        plan.setTotalPrice(requestDto.getTotalPrice());

        // 3. 기존에 있던 모든 장소(PlanPlace)들을 DB에서 삭제합니다.
        planPlaceRepository.deleteAll(plan.getPlanPlaces());
        plan.getPlanPlaces().clear(); // plan 엔티티의 리스트에서도 비워줍니다.

        // 4. 요청받은 새로운 장소 목록으로 PlanPlace를 다시 생성하여 추가합니다.
        for (com.example.moneyway.plan.dto.request.PlanPlaceUpdateRequestDto placeDto : requestDto.getPlaces()) {
            com.example.moneyway.place.domain.Place place = placeRepository.findById(placeDto.getPlaceId())
                    .orElseThrow(() -> new CustomPlanException(ErrorCode.PLACE_NOT_FOUND));

            PlanPlace planPlace = PlanPlace.builder()
                    .place(place)
                    .cost(placeDto.getCost())
                    .dayNumber(placeDto.getDayNumber())
                    .startTime(placeDto.getStartTime())
                    .endTime(placeDto.getEndTime())
                    .build();

            plan.addPlanPlace(planPlace);
        }
    }

    @Transactional
    public void deletePlan(Long planId, User user) {
        Plan plan = findPlanByIdAndValidateOwner(planId, user.getId());
        planRepository.delete(plan);
    }

    private Plan findPlanByIdAndValidateOwner(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomPlanException(ErrorCode.PLAN_NOT_FOUND));

        if (!plan.getUser().getId().equals(userId)) {
            throw new CustomPlanException(ErrorCode.PLAN_ACCESS_DENIED);
        }
        return plan;
    }
}