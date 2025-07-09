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
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    /**
     * 토큰에서 사용자 정보를 기반으로 Authentication 객체를 생성하는 메서드
     */
    public Authentication getAuthentication(String token) {
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