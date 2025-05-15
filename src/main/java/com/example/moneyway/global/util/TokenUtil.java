package com.example.moneyway.global.util;

import com.example.moneyway.global.exception.CustomException.CustomUserException;
import com.example.moneyway.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class TokenUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    /**
     * 요청에서 AccessToken을 추출 (Authorization 헤더 → 쿠키 순)
     */
    public static String resolveToken(HttpServletRequest request) {
        // 1️⃣ Authorization 헤더 우선
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

        // 3️⃣ 토큰이 없으면 예외
        throw new CustomUserException(ErrorCode.JWT_INVALID);
    }
}
