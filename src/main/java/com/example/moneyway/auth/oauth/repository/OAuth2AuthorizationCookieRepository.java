package com.example.moneyway.auth.oauth.repository;

import com.example.moneyway.common.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

/**
 * OAuth2 로그인 인가 요청 상태를 세션이 아닌 쿠키에 저장하기 위한 커스텀 저장소
 * 역할 : Spring Security OAuth2 로그인 과정에서 상태 정보를 안전하게 저장/복원함
 */
public class OAuth2AuthorizationCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // ✅ 쿠키 이름 상수 정의
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    // ✅ 쿠키 만료 시간 (초) - 보안 및 UX를 고려한 1시간 설정
    private static final int COOKIE_EXPIRE_SECONDS = 3600;


    /**
     * ✅ OAuth2AuthorizationRequest를 쿠키에 직렬화하여 저장하는 메서드
     * 역할 : 로그인 시작 시 상태 정보를 쿠키에 안전하게 보관함
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        // 요청 객체가 null이면 기존 쿠키 제거 (로그아웃 또는 오류 상황)
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // 요청 객체를 직렬화하여 쿠키로 저장 (Set-Cookie 헤더로 응답에 포함됨)
        CookieUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECONDS
        );
    }

    /**
     * 저장된 OAuth2AuthorizationRequest를 쿠키에서 읽어와 역직렬화하는 메서드
     * 역할 : 로그인 성공 이후 이전 요청 정보를 복원하기 위해 호출됨
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // 쿠키에서 "oauth2_auth_request"라는 이름의 쿠키를 찾음
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        // 쿠키가 없으면 null 반환하여 NPE 방지
        if (cookie == null) {
            return null;
        }

        // 찾은 쿠키를 역직렬화하여 OAuth2AuthorizationRequest 객체로 복원
        /**
         * 문자열 → 객체 (역직렬화)
         * - 쿠키에서 읽은 값을 다시 객체로 복원
         */
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }


    /**
     * ✅ 요청 정보를 제거하는 메서드 (하지만 실제로는 load만 함)
     * 역할 : 인터페이스 요구사항 충족용 (Spring 내부용)
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        // 실제 삭제는 하지 않고, loadAuthorizationRequest()만 호출
        return this.loadAuthorizationRequest(request);
    }

    /**
     * ✅ 쿠키에서 인가 요청 정보를 제거하는 메서드
     * 역할 : 인증 성공 이후 불필요한 쿠키를 삭제하여 보안 강화
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        // "oauth2_auth_request" 쿠키를 삭제 (Max-Age=0 설정)
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 사용자가 OAuth2 로그인 요청을 시도하면 Spring Security는 OAuth2AuthorizationRequest 객체를 생성함
 * 2. saveAuthorizationRequest()가 호출되어 이 객체를 직렬화 후 쿠키에 저장함 (세션 사용하지 않음)
 * 3. 사용자가 로그인 인증 후 리디렉션되면, loadAuthorizationRequest()가 호출되어 쿠키에서 이전 요청 정보를 복원함
 * 4. 인증이 성공적으로 완료되면 removeAuthorizationRequestCookies()가 호출되어 쿠키를 삭제함
 *
 * → 요약 흐름: 로그인 시도 → 요청 직렬화 & 저장 → 인증 응답 시 복원 → 성공 시 삭제
 */
