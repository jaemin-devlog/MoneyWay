// ✅ 사용자 정보 응답 DTO
package com.example.moneyway.user.dto.response;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * JWT 인증 이후 사용자 정보를 응답할 때 사용하는 DTO
 * 필드: ID, 이메일, 닉네임, 로그인 타입, 생성/수정일 등 포함
 * UserResponse: 로그인/회원가입 후 사용자 전체 정보 반환용
 */
@Getter
public class UserResponse {

    private final Long id;
    private final String kakaoId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final String loginType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public UserResponse(Long id,
                        String kakaoId,
                        String email,
                        String nickname,
                        String profileImageUrl,
                        String loginType,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.kakaoId = kakaoId;
        this.email = email;
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
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .loginType(user.getLoginType().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

