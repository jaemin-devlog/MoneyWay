package com.example.moneyway.auth.controller;

import com.example.moneyway.auth.dto.CreateAccessTokenResponse;
import com.example.moneyway.auth.token.service.TokenService;
import com.example.moneyway.common.util.CookieUtil; // ✅ CookieUtil 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue; // ✅ @CookieValue 임포트
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 클라이언트가 RefreshToken을 이용해 AccessToken을 재발급 받을 수 있도록 처리하는 API 컨트롤러
 * 역할: /api/token/reissue 엔드포인트를 통해 새로운 AccessToken 발급
 */
@Tag(name = "인증 - 토큰", description = "토큰 재발급 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class TokenApiController {

    private final TokenService tokenService;

    /**
     * ✅ HttpOnly 쿠키에 담긴 RefreshToken을 전달받아 AccessToken을 새로 발급하는 메서드
     * 요청: Body 없음 (브라우저가 쿠키를 자동으로 전송)
     * 응답: { accessToken: "..." }
     */
    @Operation(
            summary = "Access Token 재발급",
            description = "HttpOnly 쿠키에 담겨 전달된 유효한 Refresh Token을 사용하여 새로운 Access Token을 발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @ApiResponse(responseCode = "401", description = "쿠키가 없거나, 유효하지 않거나, 만료된 Refresh Token")
    @PostMapping("/refresh")
    public ResponseEntity<CreateAccessTokenResponse> reissueAccessToken(
            // ✅ [핵심] @RequestBody 대신 @CookieValue를 사용하여 쿠키에서 값을 직접 추출합니다.
            @CookieValue(CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken
    ) {
        // 1️⃣ 전달받은 RefreshToken으로 새 AccessToken 생성 시도
        String newAccessToken = tokenService.reissueAccessToken(refreshToken);

        // 2️⃣ 생성된 AccessToken을 JSON 형태로 반환 (200 OK)
        return ResponseEntity.ok(new CreateAccessTokenResponse(newAccessToken));
    }
}