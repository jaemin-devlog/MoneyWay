package com.example.moneyway.cart.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartItemRequestDto {

    /**
     * 장바구니에 새로운 아이템을 추가할 때 사용하는 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class Add {
        private Long placeId; // 어떤 장소를?
        private int quantity; // 몇 개를?

        public Add(Long placeId, int quantity) {
            this.placeId = placeId;
            this.quantity = quantity;
        }
    }

    /**
     * 장바구니 아이템의 수량을 변경할 때 사용하는 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class Update {
        private int quantity; // 몇 개로 변경할지?

        public Update(int quantity) {
            this.quantity = quantity;
        }
    }
}