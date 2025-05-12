package com.example.moneyway.auth.jwt;

import com.example.moneyway.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private Key secretKey;

    private final long accessTokenExpirationMillis  = Duration.ofHours(2).toMillis();   // 2시간
    private final long refreshTokenExpirationMillis = Duration.ofDays(14).toMillis();  // 14일

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String createAccessToken(User user) {
        return createToken(user, accessTokenExpirationMillis);
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenExpirationMillis);
    }

    private String createToken(User user, long expirationMillis) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** 새로 추가하는 부분 – 클레임에서 userId 꺼내기 **/
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        Object raw   = claims.get("userId");
        if (raw instanceof Number) {
            return ((Number) raw).longValue();
        }
        return Long.parseLong(raw.toString());
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
