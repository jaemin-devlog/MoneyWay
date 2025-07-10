package com.example.moneyway.cart.dto;

import com.example.moneyway.place.domain.PlaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

public class CartRequest {

    @Getter
    public static class AddCartItemDto {
        @NotBlank(message = "장소 ID는 필수입니다.")
        private String placeId;

        @NotNull(message = "장소 타입은 필수입니다.")
        private PlaceType placeType;

        @PositiveOrZero(message = "예상 비용은 0 이상이어야 합니다.")
        private int estimatedCost;

        private String memo;
    }

    @Getter
    public static class UpdateCartItemDto {
        @PositiveOrZero(message = "예상 비용은 0 이상이어야 합니다.")
        private int estimatedCost;

        private String memo;
    }
}