package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.request.LoginRequest;
import com.example.moneyway.user.dto.request.SignupRequest;
import com.example.moneyway.user.dto.response.AuthResponse;
import com.example.moneyway.user.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 사용자 인증(회원가입, 로그인)을 전담하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    /**
     * ✅ 회원가입
     * @Valid를 통해 Request DTO의 유효성 검사를 수행합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = userAuthService.signup(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        // Principal.getName()은 일반적으로 사용자의 식별자(여기서는 이메일)를 반환합니다.
        userAuthService.logout(principal.getName());
        return ResponseEntity.ok().build();
    }
}