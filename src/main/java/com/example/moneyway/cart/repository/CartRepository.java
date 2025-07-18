package com.example.moneyway.cart.repository;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 특정 사용자의 모든 장바구니 항목을 조회합니다.
     * @param user 조회할 사용자
     * @return 사용자의 장바구니 항목 리스트
     */
    List<Cart> findByUser(User user);

    /**
     * 특정 사용자와 장소에 해당하는 장바구니 항목이 존재하는지 확인합니다.
     * 장바구니에 중복 추가를 방지하는 로직에서 유용하게 사용됩니다.
     * @param user 사용자
     * @param place 장소
     * @return 존재 여부 (true/false)
     */
    boolean existsByUserAndPlace(User user, Place place);

    /**
     * 특정 사용자와 장소에 해당하는 장바구니 항목을 조회합니다.
     */
    Optional<Cart> findByUserAndPlace(User user, Place place);

    /**
     * 특정 장소를 포함하는 모든 장바구니 항목을 삭제합니다.
     */
    void deleteAllByPlace(Place place);

    /**
     * 장바구니 항목 ID와 사용자 ID를 사용하여 특정 장바구니 항목을 조회합니다.
     * PlanService에서 사용자가 자신의 장바구니 항목으로만 계획을 생성하도록 보장하는 데 사용됩니다.
     * @param cartId 조회할 장바구니 항목의 ID
     * @param userId 소유자 사용자의 ID
     * @return Cart 엔티티 Optional
     */
    Optional<Cart> findByIdAndUserId(Long cartId, Long userId);
}