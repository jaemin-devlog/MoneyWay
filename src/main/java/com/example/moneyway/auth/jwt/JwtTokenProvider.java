package com.example.moneyway.auth.jwt;

import com.example.moneyway.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * JWT 생성/검증/파싱을 전담하는 컴포넌트
 * 역할: AccessToken, RefreshToken을 생성하고, 이를 검증하거나 사용자 정보를 추출함
 */
@RequiredArgsConstructor
@Service
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties; // application.yml의 jwt 설정 주입 (issuer, secretKey)

    /**
     * ✅ JWT 토큰을 생성하는 메서드
     * 구조: User 객체 + 만료시간을 받아 -> JWT 문자열을 생성함
     * 반환값: 서명 포함된 JWT 문자열 (access/refresh 용도 모두 사용 가능)
     */
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());

        return makeToken(expiry, user);
    }

    /**
     * ✅ JWT 내부 구조 구성: 헤더 + 클레임 + 서명 → compact 직렬화
     * - sub: 사용자 이메일
     * - claim("id"): 사용자 ID
     * - exp: 만료시간
     * - iss: 발급자 정보
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // typ: JWT
                .setIssuer(jwtProperties.getIssuer())         // iss: 토큰 발급자
                .setIssuedAt(now)                             // iat: 발급 시간
                .setExpiration(expiry)                        // exp: 만료 시간
                .setSubject(user.getEmail())                  // sub: 사용자 이메일
                .claim("id", user.getId())              // 사용자 ID 포함 (추후 DB 조회용)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();                                   // 직렬화된 JWT 문자열 반환
    }

    /**
     * ✅ 토큰의 유효성을 검증하는 메서드
     * 구조: 서명 유효성 검사 + 파싱 가능 여부 확인
     * 반환값: 유효하면 true, 아니면 false
     */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token); // 서명 및 구조 검증 수행
            return true;
        } catch (Exception e) {
            return false; // 유효하지 않은 경우 false 반환
        }
    }

    /**
     * ✅ 토큰에서 사용자 정보를 기반으로 Authentication 객체를 생성하는 메서드
     * 구조: Claims 파싱 → 사용자 식별 정보 추출 → Spring Security 인증 객체로 변환
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // 고정된 권한 부여 (ROLE_USER)
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), // email
                        "",
                        authorities
                ),
                token,
                authorities
        );
    }

    /**
     * ✅ 토큰에서 사용자 ID (Long)를 추출하는 메서드
     * 용도: DB에서 사용자 조회할 때 식별자로 사용
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    /**
     * ✅ JWT에서 Claims(=payload 데이터)를 추출하는 내부 메서드
     * 구조: 서명 키 기반 파싱 수행 후, claim body 반환
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 사용자의 로그인/인증 성공 후 JwtTokenProvider.generateToken() 호출됨
 * 2. User 객체 정보와 만료시간을 기반으로 JWT 문자열 생성
 * 3. 해당 JWT는 AccessToken/RefreshToken 용도로 사용되며, 쿠키에 저장되거나 헤더에 포함됨
 * 4. 클라이언트가 API 요청 시 토큰을 전송하면, validToken()으로 유효성 확인
 * 5. getAuthentication()을 통해 Spring Security의 인증 객체로 변환되어 SecurityContext에 저장됨
 * 6. 필요 시 getUserId()로 토큰에서 사용자 ID만 추출 가능 (DB 조회 등에 사용)
 *
 * → 요약 흐름: 사용자 인증 → JWT 생성 → 클라이언트 저장 → 요청 시 검증/인증 추출
 */