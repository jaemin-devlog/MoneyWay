package com.example.moneyway.user.controller;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.global.exception.CustomException.CustomUserException;
import com.example.moneyway.global.exception.ErrorCode;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserInfoController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(HttpServletRequest request) {
        String token = resolveToken(request);

        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    private String resolveToken(HttpServletRequest request) {
        // 1️⃣ Authorization 헤더 우선
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2️⃣ 쿠키에 access_token 존재 여부 확인
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new CustomUserException(ErrorCode.JWT_INVALID);
    }
}
