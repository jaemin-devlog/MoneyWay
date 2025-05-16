package com.example.moneyway.auth.controller;

import com.example.moneyway.auth.dto.CreateAccessTokenRequest;
import com.example.moneyway.auth.dto.CreateAccessTokenResponse;
import com.example.moneyway.auth.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController // ✅ 꼭 필요
@RequestMapping("/api/auth/token/token") //
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<CreateAccessTokenResponse> reissueAccessToken(
            @RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.reissueAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
