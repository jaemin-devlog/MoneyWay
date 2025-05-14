package com.example.moneyway.auth.oauth;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.global.util.CookieUtil;
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

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 쿠키 이름 및 토큰 유효시간 설정
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);     // 리프레시 토큰 2주
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);      // 액세스 토큰 1시간

    // 로그인 후 리다이렉트할 프론트엔드 경로
    public static final String REDIRECT_PATH = "http://localhost:3000/oauth-success";

    // 의존성 주입된 구성 요소들
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    // 카카오 로그인 성공 후 실행되는 메서드 (핵심 진입점)
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1️⃣ 인증된 사용자 정보 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // 2️⃣ 이메일로 사용자 조회
        User user = userService.findByEmail(email);

        // 3️⃣ JWT 발급 (AccessToken / RefreshToken)
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        // 4️⃣ RefreshToken DB 저장 및 쿠키에 저장
        saveRefreshToken(user.getId(), refreshToken);
        addCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        // 5️⃣ AccessToken 쿠키 저장 (HttpOnly)
        addCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());

        // 6️⃣ 인증 관련 쿠키 삭제 (OAuth2 로그인 관련 쿠키 제거)
        clearAuthenticationAttributes(request, response);

        // 7️⃣ 최종 리다이렉트 (프론트로 이동)
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH);
    }

    // RefreshToken을 DB에 저장하거나 갱신
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        User user = userService.findById(userId);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 쿠키 추가 유틸 (기존 쿠키 제거 후 새로 추가)
    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        CookieUtil.deleteCookie(request, response, name); // ✅ 수정된 부분
        CookieUtil.addCookie(response, name, value, maxAge);
    }


    // OAuth2 로그인 관련 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
