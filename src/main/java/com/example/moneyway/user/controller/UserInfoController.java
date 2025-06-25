package com.example.moneyway.user.controller;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.common.util.TokenUtil;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 현재 로그인된 사용자의 정보를 반환하는 컨트롤러
 * 역할: JWT로 인증된 사용자 식별 후 사용자 정보 조회 및 반환
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserInfoController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * ✅ 인증된 사용자 자신의 정보를 반환하는 API
     * 경로: GET /api/users/me
     * 절차:
     * 1. 요청에서 JWT 추출 (Authorization 헤더 or 쿠키)
     * 2. 유효성 검증 후 userId 추출
     * 3. DB에서 해당 userId로 사용자 조회
     * 4. UserResponse 형태로 반환
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(HttpServletRequest request) {
        // JWT 추출 및 유효성 검사
        String token = TokenUtil.resolveToken(request);

        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        // 토큰에서 사용자 ID 추출 후 사용자 조회
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userService.findById(userId);

        return ResponseEntity.ok(UserResponse.from(user));
    }
}

/**
 * ✅ 전체 동작 흐름 요약
 *
 * 1. 클라이언트가 JWT를 포함한 요청을 보냄 (헤더 or 쿠키)
 * 2. TokenUtil이 토큰을 추출하고 JwtTokenProvider가 유효성 검사
 * 3. 토큰에서 사용자 ID를 추출하고, UserService로 DB에서 사용자 조회
 * 4. User 엔티티를 UserResponse로 변환하여 JSON 응답
 *
 * → 사용자 인증 → 정보 조회 → DTO 변환 → 응답 반환
 */
