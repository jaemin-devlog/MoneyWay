package com.example.moneyway.auth.oauth;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.util.CookieUtil;
import com.example.moneyway.user.domain.User;
// import com.example.moneyway.user.service.UserService; // [제거] 더 이상 필요 없음
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
// import org.springframework.security.oauth2.core.user.OAuth2User; // [제거] CustomOAuth2User 사용
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
// import java.util.Map; // [제거]

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    // [개선] 하드코딩된 리다이렉트 경로 대신, 설정 파일에서 기본 경로를 주입받음
    @Value("${oauth.default-redirect-uri}")
    private String defaultRedirectUri;

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationCookieRepository authorizationRequestRepository;
    // private final UserService userService; // [제거]

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // [개선] Principal을 CustomOAuth2User로 캐스팅하여 User 엔티티를 직접 가져옴
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser(); // ❗️ DB 조회 없이 바로 User 객체 획득

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        saveRefreshToken(user, refreshToken);

        addCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());
        addCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());

        // [개선] 동적 리다이렉트 URI 결정
        String targetUrl = determineTargetUrl(request);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * [신규] 인증 요청 시 쿠키에 저장된 리다이렉트 URI를 가져오는 메서드.
     * @return 쿠키에 저장된 URI가 있으면 해당 URI, 없으면 기본 URI 반환
     */
    private String determineTargetUrl(HttpServletRequest request) {
        // [수정] loadAuthorizationRequest의 결과를 Optional.ofNullable로 감싸줍니다.
        Optional<OAuth2AuthorizationRequest> authorizationRequest =
                Optional.ofNullable(authorizationRequestRepository.loadAuthorizationRequest(request));

        return authorizationRequest
                .map(OAuth2AuthorizationRequest::getRedirectUri)
                .orElse(defaultRedirectUri);
    }

    private void saveRefreshToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        CookieUtil.deleteCookie(request, response, name);
        CookieUtil.addCookie(response, name, value, maxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}