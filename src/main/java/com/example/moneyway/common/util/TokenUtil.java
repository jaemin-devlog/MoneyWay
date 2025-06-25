package com.example.moneyway.common.util;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 클라이언트 요청에서 JWT 토큰을 추출하는 유틸 클래스
 * 역할: 우선적으로 Authorization 헤더에서, 없을 경우 쿠키에서 토큰을 추출
 */
public class TokenUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    /**
     * ✅ 요청에서 AccessToken을 추출하는 메서드
     * 구조: 1. Authorization 헤더 → 2. 쿠키 fallback → 3. 없으면 예외 발생
     * 반환: 유효한 JWT 문자열 / 없을 경우 사용자 정의 예외 발생
     */
    public static String resolveToken(HttpServletRequest request) {
        // 1️⃣ Authorization 헤더 우선 확인
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 제거 후 순수 토큰 반환
        }

        // 2️⃣ 쿠키 fallback
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 3️⃣ 헤더도 쿠키도 없으면 예외 발생 (인증 실패 처리)
        throw new CustomUserException(ErrorCode.JWT_INVALID);
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 클라이언트의 요청이 들어오면 먼저 Authorization 헤더에서 JWT를 찾음
 * 2. "Bearer {accessToken}" 형식이면 "Bearer "를 제거한 후 토큰 반환
 * 3. Authorization 헤더가 없거나 유효하지 않으면 쿠키 목록을 탐색함
 * 4. "access_token" 이름의 쿠키가 있으면 그 값을 반환함
 * 5. 둘 다 없으면 JWT_INVALID 에러 코드를 담은 예외를 발생시킴
 *
 * → 요약 흐름: 요청 수신 → 헤더 검사 → 쿠키 검사 → 토큰 반환 or 예외
 */
