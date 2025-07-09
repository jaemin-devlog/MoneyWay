package com.example.moneyway.auth.jwt;

import com.example.moneyway.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * JWT 생성/검증/파싱을 전담하는 서비스
 * 역할: AccessToken, RefreshToken을 생성하고, 이를 검증하거나 사용자 정보를 추출함
 */
@Slf4j
@Component // [개선] @Service 대신 범용적인 @Component 사용
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final Key key;

    /**
     * [개선] 생성자: JwtProperties를 직접 주입받아 설정값 관리
     * @param jwtProperties application.yml의 'jwt' 프로퍼티가 바인딩된 객체
     */
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // ✅ Base64URL 형식의 secretKey를 디코딩하여 Key 객체 생성
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtProperties.secretKey());

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰을 생성하는 메서드
     */
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());
        return makeToken(expiry, user);
    }

    /**
     * JWT 내부 구조 구성 및 직렬화
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.issuer()) // [개선] jwtProperties에서 발급자 정보 사용
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰의 유효성을 검증하는 메서드
     */
    public boolean validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 서명 검증 + 파싱
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명 또는 구조: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있거나 잘못되었습니다: {}", e.getMessage());
        }
        return false;
    }


    /**
     * 토큰에서 사용자 정보를 기반으로 Authentication 객체를 생성하는 메서드
     */
    public Authentication getAuthentication(String token) {
        try {
            // getClaims 메서드는 토큰이 유효하지 않으면 예외를 던집니다.
            Claims claims = getClaims(token);
            Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            return new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(
                            claims.getSubject(),
                            "",
                            authorities
                    ),
                    token,
                    authorities
            );
        } catch (Exception e) {
            // 토큰 파싱/검증 중 예외가 발생하면 로그를 남기고 null을 반환합니다.
            // 이렇게 하면 애플리케이션이 비정상 종료되는 것을 막을 수 있습니다.
            log.warn("❗️유효하지 않은 JWT 토큰으로 인증 객체를 생성할 수 없습니다. 메시지: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 토큰에서 사용자 ID (Long)를 추출하는 메서드
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    /**
     * JWT에서 Claims를 추출하는 내부 메서드
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}