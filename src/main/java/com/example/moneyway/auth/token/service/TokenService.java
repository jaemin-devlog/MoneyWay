package com.example.moneyway.auth.token.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    // ✅ JWT AccessToken 재발급
    public String reissueAccessToken(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("⚠️ 유효하지 않은 RefreshToken: " + refreshToken);
        }

        // DB에서 저장된 RefreshToken 조회 및 사용자 정보 추출
        RefreshToken storedToken = refreshTokenService.findByRefreshToken(refreshToken);
        Long userId = storedToken.getUser().getId();

        // 새 AccessToken 생성 (2시간 유효)
        return jwtTokenProvider.generateToken(storedToken.getUser(), Duration.ofHours(2));
    }
}
