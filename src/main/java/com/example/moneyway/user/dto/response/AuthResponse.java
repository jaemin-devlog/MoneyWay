package com.example.moneyway.user.dto.response;

import com.example.moneyway.auth.dto.TokenInfo; // 방금 만든 TokenInfo를 임포트합니다.
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 또는 회원가입 성공 시, 토큰 정보와 사용자 정보를 함께 반환하는 최종 응답 DTO
 */
@Getter
@Builder
public class AuthResponse {
    private final TokenInfo tokenInfo;
    private final UserResponse userInfo;
}