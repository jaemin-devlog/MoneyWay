package com.example.moneyway.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * JWT 토큰(Access, Refresh) 정보를 담는 DTO
 */
@Getter
@Builder
public class TokenInfo {
    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
}