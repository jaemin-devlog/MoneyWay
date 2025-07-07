package com.example.moneyway.auth.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 API 응답을 매핑하기 위한 DTO.
 * record를 사용하여 불변 객체로 만듭니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드는 무시
public record KakaoAccount(
        Long id,
        @JsonProperty("kakao_account") KakaoProfile kakaoProfile
) {
    // 중첩된 JSON 구조를 위한 내부 레코드
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoProfile(
            String email,
            Profile profile
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}

    // 외부에서 쉽게 접근할 수 있도록 편의 메서드 제공
    public String getEmail() {
        return kakaoProfile.email();
    }

    public String getNickname() {
        return kakaoProfile.profile().nickname();
    }

    public String getProfileImageUrl() {
        return kakaoProfile.profile().profileImageUrl();
    }
}