package com.example.moneyway.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니에 담긴 항목의 가격을 수정할 때 사용하는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateCartPriceRequest {

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상의 값이어야 합니다.")
    private Integer price;

    public UpdateCartPriceRequest(Integer price) {
        this.price = price;
    }
}