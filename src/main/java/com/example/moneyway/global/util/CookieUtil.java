package com.example.moneyway.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

@UtilityClass
public class CookieUtil {

    /**
     * 쿠키 추가
     * - 경로: "/" (전체 경로에서 사용 가능)
     * - HttpOnly: true (JS 접근 방지)
     * - Secure 설정은 HTTP 환경에서는 생략
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 삭제
     * - 동일한 이름과 경로로 빈 값의 쿠키를 다시 설정하여 브라우저에서 삭제되도록 함
     * - request가 null이면 아무것도 하지 않음 (안전하게 처리)
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (request == null) return; // ❗ NPE 방지 추가

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * 객체 → 문자열 (직렬화)
     * - 쿠키에 객체 정보를 저장할 수 있도록 직렬화
     */
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * 문자열 → 객체 (역직렬화)
     * - 쿠키에서 읽은 값을 다시 객체로 복원
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
