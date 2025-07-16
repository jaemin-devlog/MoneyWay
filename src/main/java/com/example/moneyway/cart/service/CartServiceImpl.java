package com.example.moneyway.cart.service;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.cart.dto.request.AddCartRequest;
import com.example.moneyway.cart.dto.request.UpdateCartPriceRequest;
import com.example.moneyway.cart.dto.response.CartItemResponse;
import com.example.moneyway.cart.dto.response.CartResponse;
import com.example.moneyway.cart.repository.CartRepository;
import com.example.moneyway.place.domain.Place;
import com.example.moneyway.place.repository.PlaceRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    /**
     * 장바구니에 장소를 추가합니다.
     * @param userId 현재 인증된 사용자의 ID
     * @param request 추가할 장소의 ID를 담은 DTO
     */
    @Override
    public void addPlaceToCart(Long userId, AddCartRequest request) {
        User user = findUserById(userId);
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다. ID: " + request.getPlaceId()));

        // [수정] 동일한 장소 중복 추가 시, 예외 대신 조용히 무시 (멱등성 보장)
        if (cartRepository.existsByUserAndPlace(user, place)) {
            return;
        }

        // Place의 현재 가격을 기준으로 장바구니 항목 생성
        Cart newCartItem = Cart.of(user, place, place.getNumericPrice());
        cartRepository.save(newCartItem);
    }

    /**
     * 사용자의 장바구니 전체 정보를 조회합니다.
     * DB에서 삭제된 장소가 있더라도 오류 없이 안전하게 조회합니다.
     * @param userId 현재 인증된 사용자의 ID
     * @return 장바구니 항목 리스트와 요약 정보를 담은 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        User user = findUserById(userId);
        List<Cart> cartItems = cartRepository.findByUser(user);

        List<CartItemResponse> itemResponses = cartItems.stream()
                // ✅ [개선] 연관된 Place가 삭제되어 null인 경우를 안전하게 필터링
                .filter(cart -> cart.getPlace() != null)
                .map(CartItemResponse::from)
                .toList();

        return new CartResponse(itemResponses);
    }

    /**
     * 장바구니에 담긴 특정 항목의 가격을 수정합니다.
     * @param userId 현재 인증된 사용자의 ID
     * @param cartId 수정할 장바구니 항목의 ID
     * @param request 새로운 가격 정보를 담은 DTO
     */
    @Override
    public void updateCartItemPrice(Long userId, Long cartId, UpdateCartPriceRequest request) {
        Cart cartItem = findCartById(cartId);
        validateCartOwner(userId, cartItem);

        // 가격 변경 로직은 Cart 엔티티에 위임
        cartItem.updatePrice(request.getPrice());
    }

    /**
     * 장바구니에서 특정 항목을 삭제합니다.
     * @param userId 현재 인증된 사용자의 ID
     * @param cartId 삭제할 장바구니 항목의 ID
     */
    @Override
    public void removeCartItem(Long userId, Long cartId) {
        Cart cartItem = findCartById(cartId);
        validateCartOwner(userId, cartItem);

        cartRepository.delete(cartItem);
    }

    // --- Private Helper Methods ---

    /**
     * ID로 User 엔티티를 조회합니다. 없으면 예외를 발생시킵니다.
     * @param userId 조회할 사용자의 ID
     * @return 조회된 User 엔티티
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    /**
     * ID로 Cart 엔티티를 조회합니다. 없으면 예외를 발생시킵니다.
     * @param cartId 조회할 장바구니 항목의 ID
     * @return 조회된 Cart 엔티티
     */
    private Cart findCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목을 찾을 수 없습니다. ID: " + cartId));
    }

    /**
     * 특정 사용자가 해당 장바구니 항목의 소유주인지 검증합니다.
     * @param userId 검증할 사용자의 ID
     * @param cart 검증할 Cart 엔티티
     */
    private void validateCartOwner(Long userId, Cart cart) {
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("해당 장바구니 항목에 대한 권한이 없습니다.");
        }
    }
}