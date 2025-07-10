package com.example.moneyway.cart.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.cart.dto.CartRequest;
import com.example.moneyway.cart.dto.CartResponse;
import com.example.moneyway.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    /**
     * 특정 여행 계획의 장바구니에 항목 추가
     */
    @PostMapping("/plans/{planId}/cart")
    public ResponseEntity<Void> addCartItem(@PathVariable Long planId,
                                            @Valid @RequestBody CartRequest.AddCartItemDto request,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.addCartItem(planId, request, userDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 여행 계획의 장바구니 조회
     */
    @GetMapping("/plans/{planId}/cart")
    public ResponseEntity<CartResponse.CartViewDto> getCart(@PathVariable Long planId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CartResponse.CartViewDto cartView = cartService.getCart(planId, userDetails);
        return ResponseEntity.ok(cartView);
    }

    /**
     * 장바구니 항목 수정
     */
    @PatchMapping("/cart/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long cartItemId,
                                               @Valid @RequestBody CartRequest.UpdateCartItemDto request,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.updateCartItem(cartItemId, request, userDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * 장바구니 항목 삭제
     */
    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.removeCartItem(cartItemId, userDetails);
        return ResponseEntity.ok().build();
    }
}