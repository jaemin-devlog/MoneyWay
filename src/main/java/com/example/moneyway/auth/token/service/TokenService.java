package com.example.moneyway.auth.token.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.common.exception.CustomException.CustomUserException; // [개선] CustomUserException 사용
import com.example.moneyway.common.exception.ErrorCode;                       // [개선] ErrorCode 사용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public String reissueAccessToken(String refreshToken) {

        // 1️⃣ 토큰 유효성 검증 (서명, 만료 등)
        if (!jwtTokenProvider.validToken(refreshToken)) {
            // [개선] ErrorCode를 사용하여 명확한 예외를 발생시킴
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        // 2️⃣ DB에서 RefreshToken을 조회하여 사용자 정보 확인
        RefreshToken storedToken = refreshTokenService.findByRefreshToken(refreshToken);

        // 3️⃣ 새로운 AccessToken 생성
        return jwtTokenProvider.generateToken(storedToken.getUser(), Duration.ofHours(1));
    }
}