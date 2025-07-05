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
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final String REDIRECT_PATH = "http://192.168.100.37:3000";

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        User user = userService.findByEmail(email);

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        // 5️⃣ RefreshToken을 DB에 저장 (기존 존재하면 업데이트)
        saveRefreshToken(user, refreshToken); // [개선] user 객체를 직접 전달

        addCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());
        addCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH);
    }

    /**
     * ✅ RefreshToken을 DB에 저장하거나 갱신하는 메서드
     * 구조: userId → RefreshToken 객체 생성 or 수정 → 저장
     */
    private void saveRefreshToken(User user, String newRefreshToken) {
        // 불필요한 DB 조회를 제거하고, 전달받은 user 객체를 바로 사용합니다.
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken)) // 기존 엔티티 수정
                .orElse(new RefreshToken(user, newRefreshToken)); // 신규 엔티티 생성

        refreshTokenRepository.save(refreshToken);
    }

    // ... (addCookie, clearAuthenticationAttributes 메서드는 동일) ...
    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        CookieUtil.deleteCookie(request, response, name);
        CookieUtil.addCookie(response, name, value, maxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
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
