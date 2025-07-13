package com.example.moneyway.user.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.common.util.CookieUtil;
import com.example.moneyway.user.dto.request.LoginRequest;
import com.example.moneyway.user.dto.request.SignupRequest;
import com.example.moneyway.user.dto.response.AuthResponse;
import com.example.moneyway.user.service.UserAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증(회원가입, 로그인)을 전담하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final CookieUtil cookieUtil;

    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14; // 14 days in seconds

    /**
     * ✅ 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = userAuthService.signup(request);
        return createAuthResponseWithCookie(response, authResponse);
    }

    /**
     * ✅ 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = userAuthService.login(request);
        return createAuthResponseWithCookie(response, authResponse);
    }

    /**
     * ✅ 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse response
    ) {
        userAuthService.logout(userDetails.getUsername());
        cookieUtil.deleteCookie(response, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);
        return ResponseEntity.ok().build();
    }

    /**
     *  AuthResponse를 받아 RefreshToken 쿠키를 설정하고 ResponseEntity를 생성하는 헬퍼 메서드
     */
    private ResponseEntity<AuthResponse> createAuthResponseWithCookie(HttpServletResponse httpServletResponse, AuthResponse authResponse) {
        cookieUtil.addCookie(
                httpServletResponse,
                CookieUtil.REFRESH_TOKEN_COOKIE_NAME,
                authResponse.getTokenInfo().refreshToken(),
                REFRESH_TOKEN_MAX_AGE
        );
        return ResponseEntity.ok(authResponse);
    }
}