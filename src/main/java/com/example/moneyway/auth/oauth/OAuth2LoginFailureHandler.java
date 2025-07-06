package com.example.moneyway.auth.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 로그인 실패 시, 에러 정보를 담아 프론트엔드로 안전하게 리다이렉트하는 핸들러.
 */
@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    // application.yml에 설정된 프론트엔드 기본 주소를 주입받습니다.
    @Value("${oauth.default-redirect-uri}")
    private String defaultRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("소셜 로그인에 실패했습니다. 에러 메시지: {}", exception.getMessage());

        // 실패 시, 프론트엔드의 로그인 페이지로 에러 정보를 담아 리다이렉트합니다.
        String targetUrl = UriComponentsBuilder.fromUriString(defaultRedirectUri)
                .path("/login") // 프론트엔드의 로그인 페이지 경로 (예: /login, /auth/login 등)
                .queryParam("error", true)
                .encode(StandardCharsets.UTF_8)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}