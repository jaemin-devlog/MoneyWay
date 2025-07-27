package com.example.moneyway.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * JWT 토큰(Access, Refresh) 정보를 담는 DTO
 * @param grantType 토큰 타입 (e.g., "Bearer")
 * @param accessToken 사용자인증 및 인가에 사용되는 토큰
 * @param refreshToken AccessToken 재발급에 사용되는 토큰
 */
public record TokenInfo(String grantType, String accessToken, @JsonIgnore String refreshToken) {
}