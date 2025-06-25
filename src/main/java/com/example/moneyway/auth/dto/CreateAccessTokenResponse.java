// ✅ AccessToken 재발급 응답 DTO
package com.example.moneyway.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 서버가 새로 발급한 AccessToken 값을 클라이언트에게 전달하는 응답 DTO
 * 사용처: TokenApiController의 반환값
 */
@Getter
@AllArgsConstructor
public class CreateAccessTokenResponse {

    // ✅ 새로 발급된 액세스 토큰 문자열
    private final String accessToken;
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 클라이언트가 { "refreshToken": "..." } 형식으로 요청을 보냄
 * 2. CreateAccessTokenRequest에 JSON이 매핑되어 컨트롤러로 전달됨
 * 3. TokenService에서 새로운 AccessToken을 발급함
 * 4. 이 값을 CreateAccessTokenResponse에 담아 JSON으로 응답함
 *
 * → 요약 흐름: 요청 DTO (refreshToken) → 재발급 → 응답 DTO (accessToken)
 */
