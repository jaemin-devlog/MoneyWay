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
import com.example.moneyway.plan.dto.response.PlanSummaryResponseDto;
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

    @Transactional
    public Plan createPlan(PlanCreateRequestDto requestDto, User user) {
        Plan plan = Plan.builder()
                .title(requestDto.getTitle())
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