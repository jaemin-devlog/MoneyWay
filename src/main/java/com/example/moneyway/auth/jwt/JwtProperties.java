package com.example.moneyway.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml의 'jwt' 하위 프로퍼티를 매핑하는 불변(immutable) 설정 클래스
 * @param issuer 토큰 발급자 정보
 * @param secretKey 토큰 서명에 사용될 비밀 키 (Base64 인코딩)
 */
@ConfigurationProperties("jwt") // "jwt" 접두사를 가진 프로퍼티를 이 레코드에 바인딩
public record JwtProperties(String issuer, String secretKey) {
}