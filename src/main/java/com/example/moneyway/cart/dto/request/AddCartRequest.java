package com.example.moneyway.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니에 장소를 추가할 때 사용하는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class AddCartRequest {

    @NotNull(message = "장소 ID는 필수입니다.")
    private Long placeId;

    public AddCartRequest(Long placeId) {
        this.placeId = placeId;
    }
}