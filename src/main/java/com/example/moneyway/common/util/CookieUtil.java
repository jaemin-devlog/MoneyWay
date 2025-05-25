package com.example.moneyway.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;

/**
 * 쿠키 생성/삭제/직렬화/역직렬화를 담당하는 유틸리티 클래스
 * 역할: HttpOnly 보안 설정이 포함된 쿠키를 편리하게 추가하거나 제거하고, 객체를 쿠키로 저장 가능하도록 지원함
 */
@UtilityClass
public class CookieUtil {

    /**
     * ✅ 쿠키 추가 메서드 (Set-Cookie 응답 헤더 구성)
     * 옵션: HttpOnly=true, Secure=false (로컬 개발용), SameSite=Lax, Path="/"
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)                  // JavaScript 접근 방지
                .secure(false)                   // HTTPS 환경에서 true로 설정 필요
                .path("/")                       // 전체 경로에서 유효
                .maxAge(Duration.ofSeconds(maxAge)) // 유효 시간 설정
                .sameSite("Lax")                // 기본 로그인 유지용 옵션 (Strict은 인증 실패 가능성 있음)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * ✅ 쿠키 삭제 메서드 (같은 이름/경로로 빈 쿠키 재설정)
     * 동작: MaxAge = 0 으로 설정하여 브라우저에서 즉시 삭제
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (request == null || request.getCookies() == null) return;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * ✅ 객체를 직렬화하여 쿠키에 저장할 수 있도록 문자열로 변환
     * 구조: SerializationUtils + Base64 URL 인코딩
     */
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * ✅ 쿠키 값을 역직렬화하여 객체로 복원하는 메서드
     * 구조: Base64 디코딩 → 직렬화 복원
     * 제네릭: 원하는 클래스 타입으로 안전하게 캐스팅됨
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. addCookie() → 응답 헤더에 보안 설정 포함된 쿠키를 추가함
 * 2. deleteCookie() → 동일 이름의 쿠키를 무효화하여 브라우저에서 제거함
 * 3. serialize() → 객체를 쿠키에 담을 수 있도록 문자열로 직렬화함
 * 4. deserialize() → 쿠키 값을 다시 객체로 복원함
 *
 * → 요약 흐름: 객체 → 쿠키에 안전 저장 & 삭제 처리 + 인증 상태 유지를 위한 핵심 유틸
 */
