package com.example.moneyway.auth.token.service;

import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * RefreshToken을 DB에서 조회하는 로직을 담당하는 서비스 계층
 * 역할: 토큰 문자열로 RefreshToken을 조회하고, 없으면 예외 처리
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * ✅ refreshToken 문자열로 DB에서 해당 토큰을 조회하는 메서드
     * 반환: 존재하면 RefreshToken 엔티티 반환 / 없으면 예외 발생
     */
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected refresh token: " + refreshToken));
    }
}
/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 사용자가 JWT 재발급 요청 시, 전달된 RefreshToken 문자열을 DB에서 조회
 * 2. RefreshTokenService가 이를 처리하며, 존재하지 않으면 예외를 발생시켜 비정상 토큰 차단
 * 3. 정상적인 토큰이면 해당 사용자 정보(User)를 추출할 수 있음
 * 4. 이후 새로운 AccessToken을 발급하거나 로그인 연장을 진행함
 *
 * → 요약 흐름: RefreshToken 전달 → DB 조회 → 검증 성공 시 사용자 인증 흐름 진행
 */
