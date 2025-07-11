package com.example.moneyway.auth.oauth;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.util.CookieUtil;
import com.example.moneyway.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

/**
 * OAuth2 로그인 성공 후, JWT 토큰을 생성하여 쿠키에 담고 프론트엔드로 리다이렉트하는 핸들러.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    @Value("${oauth.default-redirect-uri}")
    private String defaultRedirectUri;

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationCookieRepository authorizationRequestRepository;
    private final CookieUtil cookieUtil; // ✅ [추가] CookieUtil을 주입받습니다.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // CustomOAuth2User에서 User 정보를 직접 가져와 불필요한 DB 조회를 방지합니다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // Access Token과 Refresh Token 생성
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        // Refresh Token을 DB에 저장 또는 업데이트
        saveRefreshToken(user, refreshToken);

        // 기존 쿠키를 삭제하고 새로운 토큰을 쿠키에 추가
        addTokensToCookie(request, response, accessToken, refreshToken);

        // 인증 관련 임시 쿠키들을 정리
        clearAuthenticationAttributes(request, response);

        // 설정된 프론트엔드 주소로 리다이렉트
        String targetUrl = determineTargetUrl(request, response, authentication);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));
        refreshTokenRepository.save(refreshToken);
    }

    private void addTokensToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken) {
        // ✅ [수정] 불필요한 request 인자를 제거하여 메서드 시그니처를 맞춥니다.
        cookieUtil.deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
        cookieUtil.deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);

        cookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());
        cookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // UriComponentsBuilder를 사용하여 안전하게 URL을 생성합니다.
        return UriComponentsBuilder.fromUriString(defaultRedirectUri)
                .build().toUriString();
    }

    /**
     * 인증 과정에서 사용된 임시 쿠키(OAuth2AuthorizationRequest)를 삭제합니다.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}