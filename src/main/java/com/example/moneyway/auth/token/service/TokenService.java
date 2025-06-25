package com.example.moneyway.auth.token.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * RefreshToken을 이용하여 새로운 AccessToken을 발급하는 서비스
 * 역할: 전달받은 RefreshToken의 유효성을 확인하고, 그 사용자에 대해 새 AccessToken을 생성함
 */
@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * ✅ 전달받은 RefreshToken으로 AccessToken을 재발급하는 메서드
     * 과정: 1. 유효성 검사 → 2. DB에서 사용자 조회 → 3. 새 토큰 생성
     * 반환: 유효한 새 AccessToken 문자열
     */
    public String reissueAccessToken(String refreshToken) {

        // 1️⃣ 토큰 유효성 검증 (서명, 만료 등)
        if (!jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("\u26a0\ufe0f 유효하지 않은 RefreshToken: " + refreshToken);
        }

        // 2️⃣ DB에서 RefreshToken을 조회하여 사용자 정보 확인
        RefreshToken storedToken = refreshTokenService.findByRefreshToken(refreshToken);
        Long userId = storedToken.getUser().getId();

        // 3️⃣ 새로운 AccessToken 생성 (지속 시간은 2시간 기준)
        return jwtTokenProvider.generateToken(storedToken.getUser(), Duration.ofHours(2));
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 클라이언트가 보낸 RefreshToken을 받아 유효성 검사 수행
 * 2. 해당 RefreshToken이 DB에 존재하는지 확인하고, 그에 연결된 사용자 엔티티 추출
 * 3. 해당 사용자 정보를 바탕으로 AccessToken을 새로 생성
 * 4. 생성된 토큰은 컨트롤러를 통해 클라이언트에 반환됨
 *
 * → 요약 흐름: RefreshToken 수신 → 검증 + DB조회 → AccessToken 생성 → 반환
 */
