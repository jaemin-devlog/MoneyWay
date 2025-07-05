package com.example.moneyway.auth.controller;

import com.example.moneyway.auth.dto.CreateAccessTokenRequest;
import com.example.moneyway.auth.dto.CreateAccessTokenResponse;
import com.example.moneyway.auth.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 클라이언트가 RefreshToken을 이용해 AccessToken을 재발급 받을 수 있도록 처리하는 API 컨트롤러
 * 역할: /api/auth/token 엔드포인트를 통해 새로운 AccessToken 발급
 */
@Tag(name = "인증 - 토큰", description = "토큰 재발급 관련 API") // [개선] Swagger 태그 추가
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class TokenApiController {

    private final TokenService tokenService;

    /**
     * ✅ POST 요청으로 RefreshToken을 전달받아 AccessToken을 새로 발급하는 메서드
     * 요청: { refreshToken: "..." }
     * 응답: { accessToken: "..." }
     */
    @Operation(summary = "Access Token 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.") // [개선] Swagger Operation 추가
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = CreateAccessTokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 Refresh Token")
    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> reissueAccessToken(
            @Valid @RequestBody CreateAccessTokenRequest request) { // [개선] @Valid 추가

        // 1️⃣ 전달받은 RefreshToken으로 새 AccessToken 생성 시도
        String newAccessToken = tokenService.reissueAccessToken(request.getRefreshToken());

        // 2️⃣ 생성된 AccessToken을 JSON 형태로 반환 (200 OK)
        // [개선] 성공적인 작업 완료를 의미하는 200 OK 상태 코드로 변경
        return ResponseEntity.ok(new CreateAccessTokenResponse(newAccessToken));
    }
}