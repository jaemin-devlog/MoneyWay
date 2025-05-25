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

/**
 * 모든 HTTP 요청에 대해 JWT 토큰을 검사하고 인증 객체를 설정하는 필터
 * 역할: 요청 헤더 또는 쿠키에 포함된 JWT를 파싱하고, 유효하면 Spring Security 인증 컨텍스트에 등록
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // ✅ Authorization 헤더 이름 및 접두어
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * ✅ 요청마다 1회 실행되는 필터 메서드
     * 구조: JWT 추출 → 유효성 검사 → 인증 정보 설정 → 필터 체인 진행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1️⃣ Authorization 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        // 2️⃣ 토큰이 존재하고 유효한 경우 → 인증 처리
        if (token != null && jwtTokenProvider.validToken(token)) {
            // JWT에서 사용자 정보 추출 후 인증 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContext에 인증 객체 등록 → 이후 @AuthenticationPrincipal 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3️⃣ 다음 필터로 요청 전달 (항상 수행)
        filterChain.doFilter(request, response);
    }

    /**
     * ✅ Authorization 헤더에서 "Bearer {token}" 형식의 JWT 추출
     * 반환: 유효한 형식이면 토큰 문자열 / 아니면 null
     */
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length()); // "Bearer " 제거 후 반환
        }
        return null;
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 사용자의 요청이 들어올 때, Authorization 헤더에서 "Bearer {token}" 형식의 JWT를 추출
 * 2. jwtTokenProvider.validToken()으로 서명 검증 및 만료 여부 확인
 * 3. 유효한 토큰이면 getAuthentication()으로 사용자 정보를 바탕으로 인증 객체 생성
 * 4. 해당 인증 객체를 SecurityContextHolder에 등록하여 Spring Security 인증 완료
 * 5. 이후 컨트롤러에서 @AuthenticationPrincipal 등을 통해 사용자 정보를 조회할 수 있음
 *
 * → 요약 흐름: 요청 수신 → 토큰 추출 → 검증 → 인증 등록 → 다음 필터로 전달
 */
