package com.example.moneyway.auth.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {
    private String kakaoId;
    private String email;
    private String nickname;
}