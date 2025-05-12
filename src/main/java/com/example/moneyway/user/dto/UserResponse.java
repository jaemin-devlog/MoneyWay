package com.example.moneyway.user.dto;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {

    private final Long id;
    private final String kakaoId;
    private final String email;             // ✅ 추가
    private final String nickname;
    private final String profileImageUrl;
    private final String loginType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public UserResponse(Long id,
                        String kakaoId,
                        String email,               // ✅ 추가
                        String nickname,
                        String profileImageUrl,
                        String loginType,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.kakaoId = kakaoId;
        this.email = email;             // ✅ 추가
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.loginType = loginType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .email(user.getEmail())           // ✅ 추가
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .loginType(user.getLoginType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
