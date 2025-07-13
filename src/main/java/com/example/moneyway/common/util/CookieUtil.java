package com.example.moneyway.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

/**
 * 쿠키 생성/삭제/조회/직렬화를 담당하는 유틸리티 클래스
 * 역할: HttpOnly 보안 설정이 포함된 쿠키를 편리하게 관리하고, 객체를 쿠키로 저장 가능하도록 지원함
 */
@Component
public class CookieUtil {

    // ✅ [개선] 쿠키 이름을 한 곳에서 상수로 관리하여 실수를 방지합니다.
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Value("${cookie.secure:true}") // ✅ [개선] 기본값을 true로 하여 보안 강화
    private boolean isSecure;

    /**
     * 이름으로 쿠키를 찾아 그 값을 Optional로 반환합니다.
     * @param request HttpServletRequest
     * @param name 찾고자 하는 쿠키의 이름
     * @return 쿠키 값 (Optional)
     */
    public Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("None") // CORS 환경에서는 "None"이 필요할 수 있습니다.
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0) // 쿠키 즉시 만료
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());
    }

    // 직렬화/역직렬화 메서드는 상태를 사용하지 않으므로 static으로 유지
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