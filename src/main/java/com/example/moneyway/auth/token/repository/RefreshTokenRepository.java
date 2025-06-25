package com.example.moneyway.auth.token.repository;

import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RefreshToken 엔티티를 DB에 접근하기 위한 Spring Data JPA 리포지토리
 * 역할: 사용자 ID 또는 토큰 문자열 기반으로 토큰을 조회하거나 저장함
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // ✅ user_id로 RefreshToken 조회 (JPA 자동으로 FK로 변환)
    Optional<RefreshToken> findByUser(User user);

    // ✅ userId(Long)로 조회 (단순 FK ID 조회)
    Optional<RefreshToken> findByUserId(Long userId);

    // ✅ 실제 토큰 문자열로 조회 (재발급 시 사용)
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}


// ✅ RefreshTokenService: 리프레시 토큰 조회 전용 서비스 계층