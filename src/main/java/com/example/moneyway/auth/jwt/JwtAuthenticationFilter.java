package com.example.moneyway.auth.jwt;

import com.example.moneyway.common.util.CookieUtil; // ✅ [추가] CookieUtil을 import 합니다.
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ✅ [수정] 요청에서 토큰을 해석하는 메서드.
     * 1순위: 쿠키에서 Access Token을 찾습니다. (웹 브라우저 로그인)
     * 2순위: Authorization 헤더에서 Bearer 토큰을 찾습니다. (API 클라이언트, 모바일 앱 등)
     */
    private String resolveToken(HttpServletRequest request) {
        // 1. 쿠키에서 토큰 확인
        Optional<String> cookieToken = cookieUtil.getCookieValue(request, CookieUtil.ACCESS_TOKEN_COOKIE_NAME);
        if (cookieToken.isPresent()) {
            return cookieToken.get();
        }

        // 2. 헤더에서 토큰 확인
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}