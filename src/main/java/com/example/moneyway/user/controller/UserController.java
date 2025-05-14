package com.example.moneyway.user.controller;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.UserResponse;
import com.example.moneyway.user.service.UserService;
import com.example.moneyway.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        throw new IllegalArgumentException("❌ 유효하지 않은 JWT 토큰");
    }
}
