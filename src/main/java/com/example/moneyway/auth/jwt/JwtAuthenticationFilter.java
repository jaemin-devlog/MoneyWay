package com.example.moneyway.auth.jwt;

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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);

        // ✅ 토큰이 존재할 경우에만 인증 처리
        if (StringUtils.hasText(jwt)) {
            // getAuthentication 내부에서 토큰 파싱 및 검증을 한 번에 처리하도록 책임을 위임합니다.
            // 유효하지 않은 토큰(만료, 손상 등)의 경우 getAuthentication이 null을 반환하거나 예외를 던질 수 있습니다.
            // 여기서는 null을 반환하는 패턴을 가정합니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

            // 인증 정보가 성공적으로 생성되었다면 SecurityContext에 저장합니다.
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}