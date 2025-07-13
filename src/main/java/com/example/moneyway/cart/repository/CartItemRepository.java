package com.example.moneyway.cart.repository;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.cart.domain.CartItem;
import com.example.moneyway.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 장바구니에 특정 장소가 이미 담겨 있는지 확인
    Optional<CartItem> findByCartAndPlace(Cart cart, Place place);

    // 사용자 ID로 모든 장바구니 아이템을 조회 (DTO로 바로 변환하기 위함)
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.place WHERE ci.cart.user.id = :userId ORDER BY ci.id")
    List<CartItem> findAllByUserId(@Param("userId") Long userId);

    // 장바구니에 속한 모든 아이템 삭제 (플랜 생성 후 장바구니 비우기용)
    void deleteAllByCart(Cart cart);
}