package com.example.moneyway.auth.token.service;

import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomException.CustomUserException; // [개선] CustomUserException 사용
import com.example.moneyway.common.exception.ErrorCode;                       // [개선] ErrorCode 사용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                // [개선] ErrorCode를 사용하여 명확한 예외를 발생시킴
                .orElseThrow(() -> new CustomUserException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }
}