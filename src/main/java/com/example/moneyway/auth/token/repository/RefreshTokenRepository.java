package com.example.moneyway.auth.token.repository;

import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // ✅ User 객체로 직접 조회 (JPA가 user_id FK로 변환)
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByUserId(Long userId);
    // ✅ RefreshToken 문자열로 조회
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
