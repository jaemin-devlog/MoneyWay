package com.example.moneyway.auth.controller;

import com.example.moneyway.auth.dto.CreateAccessTokenRequest;
import com.example.moneyway.auth.dto.CreateAccessTokenResponse;
import com.example.moneyway.auth.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 클라이언트가 RefreshToken을 이용해 AccessToken을 재발급 받을 수 있도록 처리하는 API 컨트롤러
 * 역할: /api/auth/token/token 엔드포인트를 통해 새로운 AccessToken 발급
 */
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
    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> reissueAccessToken(
            @RequestBody CreateAccessTokenRequest request) {

        // 1️⃣ 전달받은 RefreshToken으로 새 AccessToken 생성 시도
        String newAccessToken = tokenService.reissueAccessToken(request.getRefreshToken());

        // 2️⃣ 생성된 AccessToken을 JSON 형태로 반환 (201 Created)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 클라이언트가 POST /api/auth/token/token 요청을 보냄 (Body에 refreshToken 포함)
 * 2. TokenApiController가 요청을 받아 TokenService.reissueAccessToken() 호출
 * 3. TokenService 내부에서 토큰 유효성 확인 + DB에서 RefreshToken 조회
 * 4. 정상 토큰이면 새로운 AccessToken을 생성하여 반환
 * 5. 컨트롤러는 201 상태 코드와 함께 JSON 형태로 토큰을 응답함
 *
 * → 요약 흐름: RefreshToken 전달 → 서비스 처리 → AccessToken 재발급 후 반환
 */