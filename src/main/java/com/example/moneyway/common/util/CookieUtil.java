package com.example.moneyway.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value; // [개선]
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component; // [개선]
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;

/**
 * 쿠키 생성/삭제/직렬화/역직렬화를 담당하는 유틸리티 클래스
 * 역할: HttpOnly 보안 설정이 포함된 쿠키를 편리하게 추가하거나 제거하고, 객체를 쿠키로 저장 가능하도록 지원함
 */
@UtilityClass
public class CookieUtil {

    // [개선] application.yml에서 값을 주입받아 환경에 따라 동적으로 설정
    @Value("${cookie.secure:false}")
    private boolean isSecure;

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure) // [개선] 프로퍼티 값 사용
                .path("/")
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    // ... deleteCookie, serialize, deserialize 메서드는 그대로 유지 ...
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

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}