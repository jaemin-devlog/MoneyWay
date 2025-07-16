package com.example.moneyway.cart.controller;

import com.example.moneyway.cart.dto.request.AddCartRequest;
import com.example.moneyway.cart.dto.request.UpdateCartPriceRequest;
import com.example.moneyway.cart.dto.response.CartResponse;
import com.example.moneyway.cart.service.CartService;
import com.example.moneyway.common.util.AuthUserHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final AuthUserHelper authUserHelper;

    /**
     * ✅ 장바구니에 장소 추가
     */
    @PostMapping
    public ResponseEntity<Void> addPlaceToCart(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody AddCartRequest request) {
        
        com.example.moneyway.user.domain.User user = authUserHelper.getUser(principal);
        cartService.addPlaceToCart(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ 사용자의 장바구니 조회
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal User principal) {

        com.example.moneyway.user.domain.User user = authUserHelper.getUser(principal);
        CartResponse response = cartService.getCart(user.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 장바구니 항목 가격 수정
     */
    @PatchMapping("/{cartId}")
    public ResponseEntity<Void> updateCartItemPrice(
            @AuthenticationPrincipal User principal,
            @PathVariable Long cartId,
            @Valid @RequestBody UpdateCartPriceRequest request) {
        
        com.example.moneyway.user.domain.User user = authUserHelper.getUser(principal);
        cartService.updateCartItemPrice(user.getId(), cartId, request);
        
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 장바구니 항목 삭제
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeCartItem(
            @AuthenticationPrincipal User principal,
            @PathVariable Long cartId) {
        
        com.example.moneyway.user.domain.User user = authUserHelper.getUser(principal);
        cartService.removeCartItem(user.getId(), cartId);

        return ResponseEntity.noContent().build();
    }
}