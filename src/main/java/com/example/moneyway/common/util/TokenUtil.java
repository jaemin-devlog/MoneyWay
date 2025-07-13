package com.example.moneyway.common.util;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 클라이언트 요청에서 JWT 토큰을 추출하는 유틸리티 클래스
 * 역할: 우선적으로 Authorization 헤더에서, 없을 경우 쿠키에서 토큰을 추출
 */
@Component // ✅ [변경] Spring Bean으로 전환
@RequiredArgsConstructor // ✅ [추가] final 필드에 대한 생성자 주입
public class TokenUtil {

    // ✅ [추가] CookieUtil을 의존성 주입받아 사용
    private final CookieUtil cookieUtil;

    /**
     * 요청에서 AccessToken을 추출합니다. (인스턴스 메서드로 변경)
     * @param request HttpServletRequest
     * @return 추출된 토큰 문자열
     */
    public String resolveToken(HttpServletRequest request) {
        // 1️⃣ Authorization 헤더 우선 확인
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2️⃣ [개선] CookieUtil을 사용하여 쿠키 조회 로직을 위임
        Optional<String> tokenFromCookie = cookieUtil.getCookieValue(request, CookieUtil.ACCESS_TOKEN_COOKIE_NAME);
        if (tokenFromCookie.isPresent()) {
            return tokenFromCookie.get();
        }

        // 3️⃣ 헤더도 쿠키도 없으면 예외 발생
        // 참고: 이 부분은 로직에 따라 null을 반환하거나 예외를 던질 수 있습니다.
        // 현재는 인증이 필수인 경우를 가정하여 예외를 던집니다.
        throw new CustomUserException(ErrorCode.JWT_INVALID);
    }
}