//package com.example.moneyway.cart.service;
//
//import com.example.moneyway.auth.userdetails.UserDetailsImpl;
//import com.example.moneyway.cart.domain.CartItem;
//import com.example.moneyway.place.domain.PlaceType;
//import com.example.moneyway.cart.dto.CartRequest;
//import com.example.moneyway.cart.dto.CartResponse;
//import com.example.moneyway.cart.repository.CartItemRepository;
//import com.example.moneyway.common.exception.CustomException.CustomCartException;
//import com.example.moneyway.common.exception.CustomException.CustomPlanException;
//import com.example.moneyway.common.exception.ErrorCode;
//import com.example.moneyway.plan.domain.Plan;
//import com.example.moneyway.plan.repository.PlanRepository;
//import com.example.moneyway.place.repository.RestaurantJejuRepository;
//import com.example.moneyway.place.repository.TourPlaceRepository;
//import com.example.moneyway.user.domain.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CartService {
//
//    private final CartItemRepository cartItemRepository;
//    private final PlanRepository planRepository; // Plan 소유권 확인을 위해 필요
//    private final TourPlaceRepository tourPlaceRepository;
//    private final RestaurantJejuRepository restaurantJejuRepository;
//
//    /**
//     * 특정 여행 계획의 장바구니에 장소 항목 추가
//     */
//    public void addCartItem(Long planId, CartRequest.AddCartItemDto request, UserDetailsImpl userDetails) {
//        User user = userDetails.getUser();
//        Plan plan = getPlanForUser(planId, user); // Plan 조회 및 소유권 검증
//
//        // 장소 존재 여부 확인
//        validatePlaceExists(request.getPlaceId(), request.getPlaceType());
//
//        // 해당 Plan의 장바구니에 이미 존재하는지 확인
//        if (cartItemRepository.existsByPlanAndPlaceIdAndPlaceType(plan, request.getPlaceId(), request.getPlaceType())) {
//            throw new CustomCartException(ErrorCode.ALREADY_IN_CART);
//        }
//
//        CartItem cartItem = CartItem.builder()
//                .plan(plan)
//                .user(user)
//                .placeId(request.getPlaceId())
//                .placeType(request.getPlaceType())
//                .estimatedCost(request.getEstimatedCost())
//                .memo(request.getMemo())
//                .build();
//
//        cartItemRepository.save(cartItem);
//    }
//
//    /**
//     * 특정 여행 계획의 장바구니 전체 조회 (항목 리스트 + 총 예상 비용)
//     */
//    @Transactional(readOnly = true)
//    public CartResponse.CartViewDto getCart(Long planId, UserDetailsImpl userDetails) {
//        User user = userDetails.getUser();
//        Plan plan = getPlanForUser(planId, user); // Plan 조회 및 소유권 검증
//
//        List<CartItem> items = cartItemRepository.findByPlanOrderByCreatedAtAsc(plan);
//
//        List<CartResponse.CartItemDto> itemDtos = items.stream()
//                .map(this::mapToCartItemDto)
//                .filter(dto -> dto != null)
//                .collect(Collectors.toList());
//
//        int totalCost = cartItemRepository.sumTotalCostByPlan(plan).orElse(0);
//
//        return CartResponse.CartViewDto.builder()
//                .items(itemDtos)
//                .totalEstimatedCost(totalCost)
//                .build();
//    }
//
//    /**
//     * 장바구니 항목 수정 (예상 비용, 메모)
//     */
//    public void updateCartItem(Long cartItemId, CartRequest.UpdateCartItemDto request, UserDetailsImpl userDetails) {
//        User user = userDetails.getUser();
//        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
//                .orElseThrow(() -> new CustomCartException(ErrorCode.CART_NOT_FOUND));
//
//        cartItem.update(request.getEstimatedCost(), request.getMemo());
//    }
//
//    /**
//     * 장바구니 항목 삭제
//     */
//    public void removeCartItem(Long cartItemId, UserDetailsImpl userDetails) {
//        User user = userDetails.getUser();
//        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
//                .orElseThrow(() -> new CustomCartException(ErrorCode.CART_NOT_FOUND));
//
//        cartItemRepository.delete(cartItem);
//    }
//
//    // == Private Helper Methods == //
//
//    /**
//     * Plan을 조회하고, 현재 사용자가 소유자인지 검증하는 헬퍼 메서드
//     */
//    private Plan getPlanForUser(Long planId, User user) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new CustomPlanException(ErrorCode.PLAN_NOT_FOUND));
//        if (!plan.getUser().getId().equals(user.getId())) {
//            throw new CustomPlanException(ErrorCode.FORBIDDEN_PLAN_ACCESS);
//        }
//        return plan;
//    }
//
//    /**
//     * 장소의 존재 여부를 검증하는 헬퍼 메서드
//     */
//    private void validatePlaceExists(String placeId, PlaceType placeType) {
//        boolean exists;
//        switch (placeType) {
//            case TOUR_API:
//                exists = tourPlaceRepository.existsById(placeId);
//                break;
//            case JEJU_RESTAURANT:
//                exists = restaurantJejuRepository.existsById(placeId);
//                break;
//            default:
//                throw new CustomCartException(ErrorCode.INVALID_PLACE_TYPE);
//        }
//        if (!exists) {
//            throw new CustomCartException(ErrorCode.PLACE_NOT_FOUND);
//        }
//    }
//
//    /**
//     * CartItem을 CartItemDto로 변환하는 헬퍼 메서드
//     */
//    private CartResponse.CartItemDto mapToCartItemDto(CartItem cartItem) {
//        switch (cartItem.getPlaceType()) {
//            case TOUR_API:
//                return tourPlaceRepository.findById(cartItem.getPlaceId())
//                        .map(place -> CartResponse.CartItemDto.from(cartItem, place))
//                        .orElse(null);
//            case JEJU_RESTAURANT:
//                return restaurantJejuRepository.findById(cartItem.getPlaceId())
//                        .map(place -> CartResponse.CartItemDto.from(cartItem, place))
//                        .orElse(null);
//            default:
//                return null;
//        }
//    }
//}