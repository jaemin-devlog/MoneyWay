package com.example.moneyway.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component; // ✅ [변경] @UtilityClass 대신 @Component 사용
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;

/**
 * 쿠키 생성/삭제/직렬화/역직렬화를 담당하는 유틸리티 클래스
 * 역할: HttpOnly 보안 설정이 포함된 쿠키를 편리하게 추가하거나 제거하고, 객체를 쿠키로 저장 가능하도록 지원함
 */
@Component // ✅ [변경] Spring Bean으로 선언
public class CookieUtil {


    @Value("${cookie.secure:false}")
    private boolean isSecure;

    // ✅ static 메서드가 아닌 인스턴스 메서드로 변경
    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure) // 프로퍼티 값을 올바르게 사용
                .path("/")
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite("None") // CSRF 공격 방지를 위한 설정
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    // ✅ [변경] static 메서드가 아닌 인스턴스 메서드로 변경
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (request == null || request.getCookies() == null) return;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                        .httpOnly(true)
                        .secure(isSecure)
                        .path("/")
                        .maxAge(0) // 쿠키 즉시 만료
                        .sameSite("None")
                        .build();
                response.addHeader("Set-Cookie", deleteCookie.toString());
            }
        }
    }

    // 이 메서드들은 상태(isSecure)를 사용하지 않으므로 static으로 유지해도 괜찮습니다.
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