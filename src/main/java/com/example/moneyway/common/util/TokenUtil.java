package com.example.moneyway.common.util;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass; // [개선]

/**
 * 클라이언트 요청에서 JWT 토큰을 추출하는 유틸리티 클래스
 * 역할: 우선적으로 Authorization 헤더에서, 없을 경우 쿠키에서 토큰을 추출
 */
@UtilityClass // [개선] 이 클래스가 인스턴스화되는 것을 방지
public class TokenUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    public static String resolveToken(HttpServletRequest request) {
        // 1️⃣ Authorization 헤더 우선 확인
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2️⃣ 쿠키 fallback
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 3️⃣ 헤더도 쿠키도 없으면 예외 발생
        throw new CustomUserException(ErrorCode.JWT_INVALID);
    }
}