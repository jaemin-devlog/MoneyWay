package com.example.moneyway.auth.dto;

/**
 * OAuth2 인증을 통해 카카오에서 받아온 사용자 정보를 담는 DTO
 * @param kakaoId 카카오에서 발급한 고유 ID
 * @param email 사용자 이메일
 * @param nickname 사용자 닉네임
 */
public record KakaoUserInfo(String kakaoId, String email, String nickname) {
}