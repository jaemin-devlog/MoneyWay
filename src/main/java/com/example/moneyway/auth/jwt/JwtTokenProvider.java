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

@RequiredArgsConstructor
@Service
@Component
public class JwtTokenProvider {

    // application.yml의 jwt.issuer, jwt.secret-key 설정값을 주입
    private final JwtProperties jwtProperties;

    // 주어진 사용자와 만료 시간 기준으로 JWT AccessToken 또는 RefreshToken 생성
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());
        return makeToken(expiry, user);
    }

    // JWT 구성: 헤더 + 클레임 + 서명 → compact()로 직렬화
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // typ: JWT
                .setIssuer(jwtProperties.getIssuer())         // iss: 발급자
                .setIssuedAt(now)                             // iat: 발급 시간
                .setExpiration(expiry)                        // exp: 만료 시간
                .setSubject(user.getEmail())                  // sub: 사용자 식별자 (email)
                .claim("id", user.getId())                    // custom claim: id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명
                .compact();
    }

    // 토큰 유효성 검증: 서명 확인 및 예외 처리
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // 서명 불일치, 만료 등 에러 시 false
        }
    }

    // 토큰을 기반으로 Spring Security의 인증 객체(Authentication) 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // 이 프로젝트에선 권한은 고정 (ROLE_USER)
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        // Spring Security 내부 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    // 토큰에서 사용자 ID(claim의 "id")만 추출 (DB 조회 시 사용)
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    // 토큰 디코딩 → Claims(=Payload) 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
