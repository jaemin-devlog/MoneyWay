package com.example.moneyway.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 토큰 검증/파싱을 위한 Provider 주입
    private final JwtTokenProvider jwtTokenProvider;

    // HTTP 헤더 이름 및 접두어 상수
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    // 모든 요청에서 실행되는 필터 (1회성 필터)
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1️⃣ 요청 헤더에서 AccessToken 꺼내기
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        // 2️⃣ 토큰이 유효한 경우 → 인증 처리
        if (jwtTokenProvider.validToken(token)) {
            // 토큰을 기반으로 Authentication 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContext에 등록 → 이후 컨트롤러에서 @AuthenticationPrincipal 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3️⃣ 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // "Bearer {AccessToken}" 형식에서 토큰 값만 추출하는 메서드
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null; // 헤더가 없거나 형식이 잘못된 경우
    }
}
