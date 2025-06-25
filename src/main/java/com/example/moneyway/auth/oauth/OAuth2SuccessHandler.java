package com.example.moneyway.auth.oauth;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.util.CookieUtil;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 JWT 발급 및 쿠키 저장, 사용자 리다이렉트를 처리하는 핸들러
 * 역할: 인증 성공 후 사용자 정보 기반으로 AccessToken/RefreshToken 발급, 쿠키 저장, 리프레시 토큰 DB 저장, 리다이렉트 수행
 */
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // ✅ 토큰과 쿠키 설정 관련 상수
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);  // 1시간
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); // 2주
    public static final String REDIRECT_PATH = "http://192.168.100.37:3000";

    // ✅ 의존성 주입 객체들
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationCookieRepository authorizationRequestRepository;
    private final UserService userService;

    /**
     * ✅ 인증 성공 시 실행되는 메서드
     * 구조: OAuth2User → 이메일 추출 → DB에서 User 조회 → JWT 발급 → 쿠키 저장 → 리다이렉트
     * 호출 위치: Spring Security가 OAuth2 로그인 성공 후 자동으로 이 메서드 호출
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1️⃣ 인증 객체에서 OAuth2User 정보 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2️⃣ 사용자 email 추출 → 반드시 Kakao 로그인에서 보장된 구조 기준
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // 3️⃣ 우리 시스템의 User 객체를 DB에서 조회 (존재 보장)
        User user = userService.findByEmail(email); // ❗ 없을 경우 예외 처리 필요 (현재 구조에서는 KakaoOAuth2Service에서 생성되므로 null 가능성 없음)

        // 4️⃣ JWT 발급
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        // 5️⃣ RefreshToken을 DB에 저장 (기존 존재하면 업데이트)
        saveRefreshToken(user.getId(), refreshToken);

        // 6️⃣ RefreshToken 쿠키로 저장
        addCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        // 7️⃣ AccessToken 쿠키로 저장 (HttpOnly)
        addCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());

        // 8️⃣ 인증 관련 쿠키 제거 (인가 요청 시 생성된 임시 쿠키 삭제)
        clearAuthenticationAttributes(request, response);

        // 9️⃣ 최종 리다이렉트 (프론트엔드로 이동)
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH);
    }

    /**
     * ✅ RefreshToken을 DB에 저장하거나 갱신하는 메서드
     * 구조: userId → RefreshToken 객체 생성 or 수정 → 저장
     */
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        User user = userService.findById(userId);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken)) // 기존 엔티티 수정
                .orElse(new RefreshToken(user, newRefreshToken)); // 신규 엔티티 생성

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * ✅ 쿠키를 추가하는 메서드 (기존 쿠키 삭제 후 새로 추가)
     * 구조: 기존 쿠키 삭제 → 새로 추가 → 응답 헤더에 Set-Cookie 추가
     */
    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        CookieUtil.deleteCookie(request, response, name);
        CookieUtil.addCookie(response, name, value, maxAge);
    }

    /**
     * ✅ OAuth2 로그인 시 생성된 인증 요청 쿠키를 제거하는 메서드
     * 구조: super.clearAuthenticationAttributes() → 커스텀 쿠키 삭제
     */
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request); // Spring 내부 쿠키 제거
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response); // 커스텀 쿠키 제거
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. OAuth2 로그인 성공 후 Spring Security가 onAuthenticationSuccess() 호출
 * 2. 인증된 OAuth2User 객체에서 사용자 정보를 추출 (이메일 기반)
 * 3. 사용자 이메일로 DB에서 User 엔티티 조회
 * 4. 해당 사용자로부터 AccessToken/RefreshToken 생성
 * 5. RefreshToken을 DB에 저장 (기존 있으면 update)
 * 6. AccessToken & RefreshToken을 모두 쿠키로 클라이언트에 저장 (HttpOnly)
 * 7. 인증 성공 과정에서 생긴 인가 요청 관련 임시 쿠키 제거
 * 8. 최종적으로 클라이언트를 프론트엔드 페이지로 리다이렉트
 *
 * → 요약 흐름: 인증 성공 → 유저 조회 → JWT 생성 → DB & 쿠키 저장 → 인증 쿠키 제거 → 리다이렉트
 */
