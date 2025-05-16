package com.example.moneyway.config.jwt;

import com.example.moneyway.auth.jwt.JwtProperties;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;


public class JwtFactory {

    private String subject = "test@example.com";
    private Date issuedAt = new Date(); // ✅ 수정됨
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = emptyMap();

    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;      // ✅ 수정됨
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues() {
        return JwtFactory.builder().build();
    }

    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)     // ✅ 정상 작동
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }
}
