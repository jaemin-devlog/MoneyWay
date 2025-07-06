package com.example.moneyway.auth.oauth.repository;

import com.example.moneyway.common.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor; // ✅ [추가] 생성자 자동 생성을 위해 추가
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * OAuth2 로그인 인가 요청 상태를 세션이 아닌 쿠키에 저장하기 위한 커스텀 저장소
 * 역할 : Spring Security OAuth2 로그인 과정에서 상태 정보를 안전하게 저장/복원함
 */
@Component
@RequiredArgsConstructor // ✅ [추가] final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class OAuth2AuthorizationCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 3600;

    // ✅ [추가] CookieUtil을 주입받기 위한 final 필드 선언
    private final CookieUtil cookieUtil;

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // ✅ [변경] 주입받은 cookieUtil 인스턴스를 통해 메서드를 호출합니다.
        cookieUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest), // serialize는 static 메서드이므로 그대로 둡니다.
                COOKIE_EXPIRE_SECONDS
        );
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        if (cookie == null) {
            return null;
        }
        // deserialize는 static 메서드이므로 그대로 둡니다.
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        // ✅ [변경] 주입받은 cookieUtil 인스턴스를 통해 메서드를 호출합니다.
        cookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}