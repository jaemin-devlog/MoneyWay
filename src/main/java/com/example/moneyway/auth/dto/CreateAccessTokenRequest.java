// ✅ AccessToken 재발급 요청 DTO
package com.example.moneyway.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트가 전달한 RefreshToken 값을 담는 요청 DTO
 * 사용처: TokenApiController의 reissueAccessToken() 요청 바디
 */
@Getter
@NoArgsConstructor
public class CreateAccessTokenRequest {

    // ✅ 클라이언트가 전달할 리프레시 토큰 문자열
    private String refreshToken;

    // 주의: Jackson 등에서 기본 생성자 없으면 역직렬화 실패 가능 → @NoArgsConstructor 필수
}

