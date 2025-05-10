package com.example.moneyway.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseSignupRequest {
    private String kakaoId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String gender;
    private String ageRange;
}

